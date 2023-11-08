package com.example.xmsurvey.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xmsurvey.databinding.ItemQuestionBinding

class SurveyAdapter : ListAdapter<QuestionUIModel, SurveyAdapter.QuestionViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        return QuestionViewHolder(
            ItemQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: QuestionViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = if (payloads.isEmpty()) {
        super.onBindViewHolder(holder, position, payloads)
    } else {
        payloads.flatMap { it as List<*> }
            .forEach {
                when (it) {
                    DiffUtils.AdapterPayloads.QUESTION -> {
                        holder.onBindQuestion(
                            model = getItem(position)
                        )
                    }

                    DiffUtils.AdapterPayloads.ANSWER -> {
                        holder.onBindAnswer(
                            model = getItem(position)
                        )
                    }
                }
            }
    }

    class QuestionViewHolder(
        private val binding: ItemQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: QuestionUIModel) = with(binding) {
            onBindQuestion(model = model)
            onBindAnswer(model = model)
        }

        fun onBindQuestion(model: QuestionUIModel) = with(binding) {
            questionTV.text = model.question
        }

        fun onBindAnswer(model: QuestionUIModel) = with(binding) {
            answerET.setText(model.answer)
        }
    }


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
                    AdapterPayloads.QUESTION -> oldItem.question != newItem.question
                    AdapterPayloads.ANSWER -> oldItem.answer != newItem.answer
                }
            }

        enum class AdapterPayloads {
            QUESTION,
            ANSWER
        }
    }
}