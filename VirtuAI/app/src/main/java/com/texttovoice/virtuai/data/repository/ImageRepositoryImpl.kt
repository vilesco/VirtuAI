package com.texttovoice.virtuai.data.repository

import android.util.Log
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.GeneratedImage
import com.texttovoice.virtuai.data.model.RequestBody
import com.texttovoice.virtuai.data.source.remote.ConversAIService
import com.texttovoice.virtuai.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val service: ConversAIService) :
    ImageRepository {
    override suspend fun generateImage(requestBody: RequestBody): Flow<Resource<GeneratedImage>> = flow {

        emit(Resource.Loading)

        try {
            val response = service.generateImage(requestBody)
            emit(Resource.Success(response))
        } catch (t: Throwable) {
            Log.e("ImageRepositoryImpl", "generateImage: ", t)
            emit(Resource.Error(t))
        }
    }
}