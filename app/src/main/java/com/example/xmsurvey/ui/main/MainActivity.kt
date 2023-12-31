package com.example.xmsurvey.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.xmsurvey.databinding.ActivityMainBinding
import com.example.xmsurvey.ui.survey.SurveyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startSurveyBtn.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
    }
}