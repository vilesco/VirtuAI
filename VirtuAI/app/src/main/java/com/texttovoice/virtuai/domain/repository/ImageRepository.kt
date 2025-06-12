package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.GeneratedImage
import com.texttovoice.virtuai.data.model.RequestBody
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun generateImage(requestBody: RequestBody):  Flow<Resource<GeneratedImage>>
}