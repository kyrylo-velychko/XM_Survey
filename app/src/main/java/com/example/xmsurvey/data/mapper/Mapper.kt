package com.example.xmsurvey.data.mapper

import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.data.model.QuestionItemApiModel
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.domain.model.Question

fun Answer.toApiModel() =
    AnswerItemApiModel(
        id = id,
        answer = answer
    )

fun QuestionItemApiModel.toDomain() =
    Question(
        id = id,
        question = question
    )