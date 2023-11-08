package com.example.xmsurvey.di

import com.example.xmsurvey.data.repository.SurveyRepository
import com.example.xmsurvey.data.repository.SurveyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface SurveyModule {

    @Binds
    fun provideRepository(repo: SurveyRepositoryImpl): SurveyRepository
}