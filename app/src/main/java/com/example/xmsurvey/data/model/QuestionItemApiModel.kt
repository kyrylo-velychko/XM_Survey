package com.example.xmsurvey.data.model

import com.google.gson.annotations.SerializedName

data class QuestionItemApiModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("question")
    val question: String,
)