package com.example.xmsurvey.domain.repository

import com.example.xmsurvey.base.Result
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.domain.model.Question

interface SurveyRepository {

    suspend fun downloadQuestions(): Result<List<Question>>

    suspend fun submitAnswer(answer: Answer): Result<Unit>
}