package com.example.xmsurvey.domain.repository

import com.example.xmsurvey.base.Result
import com.example.xmsurvey.data.api.ApiService
import com.example.xmsurvey.data.mapper.toApiModel
import com.example.xmsurvey.data.mapper.toDomain
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.domain.model.Question
import javax.inject.Inject

class SurveyRepositoryImpl
@Inject constructor(
    private val apiService: ApiService
) : SurveyRepository {
    override suspend fun downloadQuestions(): Result<List<Question>> {
        apiService.downloadQuestions().run {
            return if (this.isSuccessful) {
                Result.Success(this.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.Error(this.message())
            }
        }
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        apiService.submitAnswer(answer.toApiModel()).run {
            return if (this.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(this.message())
            }
        }
    }
}