package com.example.xmsurvey.data.repository

import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.data.model.QuestionItemApiModel
import retrofit2.Response

interface SurveyRepository {

    suspend fun downloadQuestions(): Response<List<QuestionItemApiModel>>

    suspend fun submitAnswer(answer: AnswerItemApiModel): Response<Unit>
}