package com.example.xmsurvey.data.model

import com.google.gson.annotations.SerializedName

data class AnswerItemApiModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("answer")
    val answer: String
)