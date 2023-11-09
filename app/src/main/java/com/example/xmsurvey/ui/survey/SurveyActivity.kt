package com.example.xmsurvey.ui.survey

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.xmsurvey.R
import com.example.xmsurvey.databinding.ActivitySurveyBinding
import com.example.xmsurvey.domain.model.Answer
import com.example.xmsurvey.ui.survey.adapter.SurveyAdapter
import com.example.xmsurvey.ui.survey.adapter.model.QuestionUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding

    private val viewModel by viewModels<SurveyViewModel>()

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initListeners()
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()

        initSubscriptions()
    }

    override fun onDestroy() {
        binding.recyclerView.removeOnScrollListener(recyclerViewOnScrollListener)
        super.onDestroy()
    }
    // endregion

    // region Subscriptions and Listeners
    private fun initListeners() = with(binding) {
        retryBtn.setOnClickListener {
            viewModel.lastNotSubmittedAnswerState.value?.let { lastNotSubmittedAnswer ->
                viewModel.sendAnswer(lastNotSubmittedAnswer)
            }
        }
    }

    private fun initSubscriptions() {
        lifecycleScope.launch {
            viewModel.surveyUIState.collect(::updateUI)
        }

        lifecycleScope.launch {
            viewModel.currentQuestionNumberState.collect {
                invalidateOptionsMenu()
            }
        }

        lifecycleScope.launch {
            viewModel.allQuestionsCounterState.collect {
                updateAllQuestionsCounter(it.first, it.second)
            }
        }

        lifecycleScope.launch {
            viewModel.isSubmitSuccessfulFlow.collectLatest {
                showBannerIsSubmitSuccessful(it)
                delay(2000)
                hideBannerIsSubmitSuccessful()
            }
        }

        lifecycleScope.launch {
            viewModel.submittedQuestionsNumber.collect {
                updateSubmittedQuestionsCounter(it)
            }
        }

        lifecycleScope.launch {
            viewModel.toastFlow.collectLatest {
                Toast.makeText(this@SurveyActivity, getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }
    // endregion

    // region RecyclerView
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

    private fun scrollRecyclerViewToPosition(shift: Int) {
        val newPosition = viewModel.currentQuestionNumberState.value + shift
        binding.recyclerView.smoothScrollToPosition(newPosition)
    }
    // endregion

    // region ActionBar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        menu?.findItem(R.id.buttonPrevious)?.isEnabled = !viewModel.isFirstQuestionState.value
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
    // endregion

    private fun updateUI(data: List<QuestionUIModel>) {
        (binding.recyclerView.adapter as SurveyAdapter).submitList(data)
    }

    private fun onSubmitClicked(answer: Answer) {
        viewModel.sendAnswer(answer)
    }

    // region Counters
    private fun updateAllQuestionsCounter(currentQuestionNumber: Int, allQuestionsNumber: Int) {
        supportActionBar?.title = if (allQuestionsNumber == 0) {
            getString(R.string.loading)
        } else {
            getString(R.string.activity_title, currentQuestionNumber, allQuestionsNumber)
        }
    }

    private fun updateSubmittedQuestionsCounter(count: Int) {
        binding.submittedQuestionsTV.text = getString(R.string.questions_submitted, count)
    }
    // endregion

    // region Success/Failure banner
    private fun showBannerIsSubmitSuccessful(isSuccess: Boolean) = with(binding) {
        bannerTV.isVisible = true
        if (isSuccess) {
            bannerTV.run {
                setText(R.string.success)
                setBackgroundColor(getColor(R.color.success))
            }
            retryBtn.isVisible = false
        } else {
            bannerTV.run {
                setText(R.string.failure)
                setBackgroundColor(getColor(R.color.failure))
            }
            retryBtn.isVisible = true
        }
    }

    private fun hideBannerIsSubmitSuccessful() {
        binding.bannerTV.isVisible = false
        binding.retryBtn.isVisible = false
    }
    // endregion
}