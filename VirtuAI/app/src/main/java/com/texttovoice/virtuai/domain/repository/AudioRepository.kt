package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.RequestBodySpeech
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface AudioRepository {
    suspend fun createSpeech(requestBody: RequestBodySpeech): Flow<Resource<ResponseBody>>

}