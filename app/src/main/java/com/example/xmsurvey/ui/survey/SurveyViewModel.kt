package com.example.xmsurvey.ui.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xmsurvey.base.Result
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.domain.repository.SurveyRepository
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel
import com.example.xmsurvey.ui.survey.adapter.model.SurveyUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val surveyRepository: SurveyRepository
) : ViewModel() {

    init {
        getQuestions()

        viewModelScope.launch {
            surveyRecyclerViewUIState
                .filter { it.isNotEmpty() }
                .collect {
                    _surveyUIState.emit(SurveyUIState.DisplaySurveyState(it))
                }
        }
    }

    private val _surveyUIState = MutableSharedFlow<SurveyUIState>()
    val surveyUIState = _surveyUIState.asSharedFlow()

    private val surveyRecyclerViewUIState = MutableStateFlow<List<QuestionUIModel>>(emptyList())

    // region current question & all questions counters
    private val _currentQuestionNumberState = MutableStateFlow(-1)
    val currentQuestionNumberState = _currentQuestionNumberState.asStateFlow()

    fun updateCurrentQuestionNumber(position: Int) = viewModelScope.launch {
        _currentQuestionNumberState.emit(position)
    }

    val allQuestionsCounterState = combine(
        _currentQuestionNumberState,
        surveyRecyclerViewUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 to surveyUI.size
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0 to 0)

    val submittedQuestionsNumber =
        surveyRecyclerViewUIState.map { it.count { it.answer.isNotEmpty() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val isFirstQuestionState = _currentQuestionNumberState.map { it == 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isLastQuestionState = combine(
        _currentQuestionNumberState,
        surveyRecyclerViewUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 == surveyUI.size && surveyUI.isNotEmpty()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    // endregion

    // region Questions list
    private fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = surveyRepository.downloadQuestions()) {
                    is Result.Success -> {
                        val questionsList = result.let {
                            it.data.map { question ->
                                QuestionUIModel(
                                    question = question.question,
                                    answer = ""
                                )
                            }
                        }

                        surveyRecyclerViewUIState.emit(questionsList)
                        updateCurrentQuestionNumber(0)
                    }

                    is Result.Error -> _surveyUIState.emit(SurveyUIState.ErrorUnsuccessfulGetQuestionsState)
                }
            } catch (e: Exception) {
                _surveyUIState.emit(SurveyUIState.ErrorGenericState)
            }
        }
    }
    // endregion

    // region Submit
    val isSubmitSuccessfulFlow = MutableSharedFlow<Boolean>()

    val lastNotSubmittedAnswerState = MutableStateFlow<Answer?>(null)

    fun sendAnswer(answer: Answer) = viewModelScope.launch(Dispatchers.IO) {
        try {
            when (surveyRepository.submitAnswer(answer)) {
                is Result.Success -> {
                    saveSubmittedAnswer(answer)
                    isSubmitSuccessfulFlow.emit(true)
                }

                is Result.Error -> {
                    lastNotSubmittedAnswerState.emit(answer)
                    isSubmitSuccessfulFlow.emit(false)
                }
            }
        } catch (e: Exception) {
            _surveyUIState.emit(SurveyUIState.ErrorGenericState)
        }
    }

    private suspend fun saveSubmittedAnswer(answer: Answer) {
        surveyRecyclerViewUIState.value[answer.id].let { answeredQuestion ->
            val updatedList = surveyRecyclerViewUIState.value.toMutableList()
            val updatedFirstQuestion = answeredQuestion.copy(answer = answer.answer)
            updatedList[answer.id] = updatedFirstQuestion
            surveyRecyclerViewUIState.emit(updatedList)
        }
    }
    // endregion
}