package com.example.xmsurvey.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xmsurvey.data.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val surveyRepository: SurveyRepository
) : ViewModel() {

    val surveyUIState = MutableStateFlow<List<QuestionUIModel>>(emptyList())

    fun getQuestions() {
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