package com.texttovoice.virtuai.data.source.remote

import com.google.gson.JsonObject
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.data.model.GeneratedImage
import com.texttovoice.virtuai.data.model.RequestBody
import com.texttovoice.virtuai.data.model.RequestBodySpeech
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ConversAIService {

    @POST(Constants.Endpoints.TEXT_COMPLETIONS)
    @Streaming
    fun textCompletionsWithStream(@Body body: JsonObject): Call<ResponseBody>

    @POST(Constants.Endpoints.TEXT_COMPLETIONS_TURBO)
    @Streaming
    fun textCompletionsTurboWithStream(@Body body: JsonObject): Call<ResponseBody>

    @POST(Constants.Endpoints.MODERATIONS)
    fun textModerations(@Body body: JsonObject): Call<ResponseBody>

    @POST(Constants.Endpoints.GENERATE_IMAGE)
    suspend fun generateImage(@Body body: RequestBody): GeneratedImage

    @POST(Constants.Endpoints.CREATE_SPEECH)
    suspend fun createSpeech(@Body body: RequestBodySpeech): Response<ResponseBody>

}