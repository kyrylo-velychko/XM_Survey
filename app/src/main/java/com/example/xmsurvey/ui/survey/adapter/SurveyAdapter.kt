package com.example.xmsurvey.ui.survey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.xmsurvey.databinding.ItemQuestionBinding
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.ui.survey.adapter.diff_util.DiffUtils
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel
import com.example.xmsurvey.ui.survey.adapter.view_holder.QuestionViewHolder

class SurveyAdapter(
    private val onSubmitClick: (Answer) -> Unit,
) : ListAdapter<QuestionUIModel, QuestionViewHolder>(DiffUtils()) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuestionViewHolder(
            binding = ItemQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onSubmitClick = onSubmitClick
        )

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) =
        holder.onBind(getItem(position))

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
                    DiffUtils.AdapterPayloads.ANSWER -> {
                        holder.onBindAnswer(
                            model = getItem(position)
                        )
                    }
                }
            }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}