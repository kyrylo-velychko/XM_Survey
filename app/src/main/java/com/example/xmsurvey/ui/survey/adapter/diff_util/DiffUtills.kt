package com.example.xmsurvey.ui.survey.adapter.diff_util

import androidx.recyclerview.widget.DiffUtil
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel

class DiffUtils : DiffUtil.ItemCallback<QuestionUIModel>() {

    override fun areItemsTheSame(
        oldItem: QuestionUIModel,
        newItem: QuestionUIModel
    ) = oldItem.question == newItem.question

    override fun areContentsTheSame(
        oldItem: QuestionUIModel,
        newItem: QuestionUIModel
    ) = oldItem == newItem

    override fun getChangePayload(oldItem: QuestionUIModel, newItem: QuestionUIModel): Any =
        AdapterPayloads.values().filter {
            when (it) {
                AdapterPayloads.ANSWER -> oldItem.answer != newItem.answer
            }
        }

    enum class AdapterPayloads {
        ANSWER
    }
}