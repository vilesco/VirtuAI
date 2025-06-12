package com.texttovoice.virtuai.data.model

import com.google.gson.annotations.SerializedName

data class RequestBodySpeech(
    @SerializedName("model")
    val model: String,
    @SerializedName("input")
    val input: String,
    @SerializedName("voice")
    val voice: String
)