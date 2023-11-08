package com.example.xmsurvey.di

import com.example.xmsurvey.data.api.RetrofitInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiNetworkModule {
    private const val BASE_URL = "https://xm-assignment.web.app"

    @Singleton
    @Provides
    fun provideRetrofit() =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideRetrofitInterface(retrofit: Retrofit) =
        retrofit.create(RetrofitInterface::class.java)
}