package com.example.xmsurvey.ui.survey.adapter.view_holder

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.xmsurvey.R
import com.example.xmsurvey.databinding.ItemQuestionBinding
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel

class QuestionViewHolder(
    private val binding: ItemQuestionBinding,
    private val onSubmitClick: (Answer) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.run {
            submitBtn.setOnClickListener {
                onSubmitClick(
                    Answer(
                        id = adapterPosition,
                        answer = binding.answerET.text.toString()
                    )
                )
            }
            answerET.doAfterTextChanged {
                submitBtn.isEnabled = it?.isEmpty() != true
            }
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
            submitBtn.setText(R.string.submit)
        }
    }
}