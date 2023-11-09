package com.example.xmsurvey.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.data.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    val isSubmitSuccessfulFlow = MutableSharedFlow<Boolean>()

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

    val isLastQuestionState = combine(
        _currentQuestionNumberState,
        surveyUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 == surveyUI.size && surveyUI.isNotEmpty()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val lastNotSavedAnswerState = MutableStateFlow<AnswerItemApiModel?>(null)

    private fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = surveyRepository.downloadQuestions()

                if (response.isSuccessful) {
                    val questionsList = response.body()?.let {
                        it.map {
                            QuestionUIModel(
                                question = it.question,
                                answer = ""
                            )
                        }
                    } ?: emptyList()

                    surveyUIState.emit(questionsList)
                } else {
                    val errorBody = response.errorBody()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sendAnswer(answer: AnswerItemApiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = surveyRepository.submitAnswer(answer)

                if (response.isSuccessful) {
                    saveSubmittedAnswer(answer)
                    isSubmitSuccessfulFlow.emit(true)
                } else {
                    lastNotSavedAnswerState.emit(answer)
                    isSubmitSuccessfulFlow.emit(false)
                }
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun saveSubmittedAnswer(answer: AnswerItemApiModel) {
        surveyUIState.value[answer.id].let { answeredQuestion ->
            val updatedList = surveyUIState.value.toMutableList()
            val updatedFirstQuestion = answeredQuestion.copy(answer = answer.answer)
            updatedList[answer.id] = updatedFirstQuestion
            surveyUIState.emit(updatedList)
        }
    }
}