package com.example.xmsurvey.data.api

import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.data.model.QuestionItemApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitInterface {

    @GET("/questions")
    suspend fun downloadQuestions(): Response<List<QuestionItemApiModel>>

    @POST("/question/submit")
    suspend fun submitAnswer(@Body answer: AnswerItemApiModel): Response<Unit>
}