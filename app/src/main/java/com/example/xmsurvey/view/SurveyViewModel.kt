package com.example.xmsurvey.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xmsurvey.data.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val _currentQuestionNumberState = MutableStateFlow(-1)
    val currentQuestionNumberState = _currentQuestionNumberState.asStateFlow()

    fun updateCurrentQuestionNumber(position: Int) = viewModelScope.launch {
        _currentQuestionNumberState.emit(position)
    }

    val isLastQuestionState = combine(
        _currentQuestionNumberState,
        surveyUIState,
    ) { currentPosition, surveyUI ->
        currentPosition + 1 == surveyUI.size && surveyUI.isNotEmpty()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

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
}