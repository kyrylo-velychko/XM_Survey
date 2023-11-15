package com.example.xmsurvey.ui.survey.adapter.model

sealed class SurveyUIState{

    data class DisplaySurveyState(val data: List<QuestionUIModel>) : SurveyUIState()

    data object ErrorGenericState : SurveyUIState()

    data object ErrorUnsuccessfulGetQuestionsState : SurveyUIState()
}