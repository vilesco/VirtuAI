package com.texttovoice.virtuai.data.repository

import android.util.Log
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.RequestBodySpeech
import com.texttovoice.virtuai.data.source.remote.ConversAIService
import com.texttovoice.virtuai.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(private val service: ConversAIService) :
    AudioRepository {

    override suspend fun createSpeech(requestBody: RequestBodySpeech): Flow<Resource<okhttp3.ResponseBody>> = flow {
        emit(Resource.Loading)

        try {
            val response = service.createSpeech(requestBody)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(Exception("Error fetching audio")))
            }
        } catch (t: Throwable) {
            Log.e("ImageRepositoryImpl", "generateImage: ", t)
            emit(Resource.Error(t))
        }
    }
}