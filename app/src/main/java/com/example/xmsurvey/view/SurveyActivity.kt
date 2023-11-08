package com.example.xmsurvey.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.xmsurvey.databinding.ActivitySurveyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding

    private val viewModel by viewModels<SurveyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getQuestions()

        initViews()
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = SurveyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this@SurveyActivity, HORIZONTAL, false)
        PagerSnapHelper().apply { attachToRecyclerView(recyclerView) }
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            viewModel.surveyUIState.collect(::updateUI)
        }
    }

    private fun updateUI(data: List<QuestionUIModel>) {
        (binding.recyclerView.adapter as SurveyAdapter).submitList(data)
    }
}