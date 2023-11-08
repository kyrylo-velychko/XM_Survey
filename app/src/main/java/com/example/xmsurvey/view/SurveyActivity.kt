package com.example.xmsurvey.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.xmsurvey.R
import com.example.xmsurvey.data.model.AnswerItemApiModel
import com.example.xmsurvey.databinding.ActivitySurveyBinding
import com.example.xmsurvey.view.adapter.SurveyAdapter
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

        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            viewModel.surveyUIState.collect(::updateUI)
        }

        lifecycleScope.launch {
            viewModel.currentQuestionNumberState.collect {
                invalidateOptionsMenu()
            }
        }
    }

    override fun onDestroy() {
        binding.recyclerView.removeOnScrollListener(recyclerViewOnScrollListener)
        super.onDestroy()
    }

    private fun initRecyclerView() = with(binding.recyclerView) {
        adapter = SurveyAdapter(::onSubmitClicked)
        layoutManager = LinearLayoutManager(this@SurveyActivity, HORIZONTAL, false)
        addOnScrollListener(recyclerViewOnScrollListener)
        PagerSnapHelper().apply { attachToRecyclerView(this@with) }
    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            viewModel.updateCurrentQuestionNumber(currentPosition)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        menu?.findItem(R.id.buttonPrevious)?.isEnabled =
            viewModel.currentQuestionNumberState.value != 0
        menu?.findItem(R.id.buttonNext)?.isEnabled = !viewModel.isLastQuestionState.value
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.buttonPrevious -> {
            scrollRecyclerViewToPosition(shift = -1)
            true
        }

        R.id.buttonNext -> {
            scrollRecyclerViewToPosition(shift = +1)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }


    private fun scrollRecyclerViewToPosition(shift: Int) {
        val newPosition = viewModel.currentQuestionNumberState.value + shift
        binding.recyclerView.smoothScrollToPosition(newPosition)
    }

    private fun updateUI(data: List<QuestionUIModel>) {
        (binding.recyclerView.adapter as SurveyAdapter).submitList(data)
    }

    private fun onSubmitClicked(answer: AnswerItemApiModel) {
        viewModel.sendAnswer(answer)
    }
}