package com.texttovoice.virtuai.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.data.source.remote.ConversAIService
import com.texttovoice.virtuai.domain.use_case.app.GetApiKeyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideOkHttpClient(apiKeyUseCase: GetApiKeyUseCase) = OkHttpClient.Builder()
        .addNetworkInterceptor(
            Interceptor { chain ->
                var request: Request? = null
                val original = chain.request()
                val apiKey = runBlocking { apiKeyUseCase() }

                val requestBuilder = original.newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                request = requestBuilder.build()
                chain.proceed(request)
            })
        .connectTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideConversAIService(retrofit: Retrofit): ConversAIService =
        retrofit.create(ConversAIService::class.java)


}