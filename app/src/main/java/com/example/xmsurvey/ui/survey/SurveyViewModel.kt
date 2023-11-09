package com.example.xmsurvey.ui.survey

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xmsurvey.R
import com.example.xmsurvey.base.Result
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.domain.repository.SurveyRepository
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    }

    val surveyUIState = MutableStateFlow<List<QuestionUIModel>>(emptyList())

    // region current question & all questions counters
    private val _currentQuestionNumberState = MutableStateFlow(-1)
    val currentQuestionNumberState = _currentQuestionNumberState.asStateFlow()

    fun updateCurrentQuestionNumber(position: Int) = viewModelScope.launch {
        _currentQuestionNumberState.emit(position)
    }

    val allQuestionsCounterState = combine(
        _currentQuestionNumberState,
        surveyUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 to surveyUI.size
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0 to 0)

    val submittedQuestionsNumber = surveyUIState.map { it.count { it.answer.isNotEmpty() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val isFirstQuestionState = _currentQuestionNumberState.map { it == 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isLastQuestionState = combine(
        _currentQuestionNumberState,
        surveyUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 == surveyUI.size && surveyUI.isNotEmpty()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    // endregion

    // region Toast
    private val _toastFlow = MutableSharedFlow<Int>()
    val toastFlow = _toastFlow.asSharedFlow()

    private fun updateToastStringRes(@StringRes resId: Int) = viewModelScope.launch {
        _toastFlow.emit(resId)
    }
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

                        surveyUIState.emit(questionsList)
                    }

                    is Result.Error -> updateToastStringRes(R.string.msg_unable_to_get_questions)
                }
            } catch (e: Exception) {
                updateToastStringRes(R.string.msg_unable_to_get_questions)
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
            updateToastStringRes(R.string.msg_unable_to_send_answer)
        }
    }

    private suspend fun saveSubmittedAnswer(answer: Answer) {
        surveyUIState.value[answer.id].let { answeredQuestion ->
            val updatedList = surveyUIState.value.toMutableList()
            val updatedFirstQuestion = answeredQuestion.copy(answer = answer.answer)
            updatedList[answer.id] = updatedFirstQuestion
            surveyUIState.emit(updatedList)
        }
    }
    // endregion
}