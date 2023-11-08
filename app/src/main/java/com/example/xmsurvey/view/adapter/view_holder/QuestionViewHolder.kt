package com.example.xmsurvey.view.adapter.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.example.xmsurvey.R
import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.databinding.ItemQuestionBinding
import com.example.xmsurvey.view.QuestionUIModel

class QuestionViewHolder(
    private val binding: ItemQuestionBinding,
    private val onSubmitClick: (AnswerItemApiModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.submitBtn.setOnClickListener {
            onSubmitClick(
                AnswerItemApiModel(
                    id = adapterPosition,
                    answer = binding.answerET.text.toString()
                )
            )
        }
    }

    fun onBind(model: QuestionUIModel) = with(binding) {
        questionTV.text = model.question
        onBindAnswer(model = model)
    }

    fun onBindAnswer(model: QuestionUIModel) = with(binding) {
        answerET.setText(model.answer)
        if (model.answer.isNotEmpty()) {
            answerET.run {
                isEnabled = false
                clearFocus()
            }
            submitBtn.run {
                isEnabled = false
                setText(R.string.already_submitted)
            }
        } else {
            answerET.isEnabled = true
            submitBtn.run {
                isEnabled = true
                setText(R.string.submit)
            }
        }
    }
}