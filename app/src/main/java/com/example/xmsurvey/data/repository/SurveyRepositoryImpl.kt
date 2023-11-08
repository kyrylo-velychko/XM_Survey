package com.example.xmsurvey.data.repository

import com.example.xmsurvey.data.api.RetrofitInterface
import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.data.model.QuestionItemApiModel
import retrofit2.Response
import javax.inject.Inject

class SurveyRepositoryImpl
@Inject constructor(
    private val retrofitInterface: RetrofitInterface
) : SurveyRepository {
    override suspend fun downloadQuestions(): Response<List<QuestionItemApiModel>> =
        retrofitInterface.downloadQuestions()


    override suspend fun submitAnswer(answer: AnswerItemApiModel): Response<Unit> =
        retrofitInterface.submitAnswer(answer)
}