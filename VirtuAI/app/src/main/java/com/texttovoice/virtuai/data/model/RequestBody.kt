package com.texttovoice.virtuai.data.model

import com.google.gson.annotations.SerializedName

data class RequestBody(
    @SerializedName("model")
    val model: String,
    @SerializedName("n")
    val n: Int,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("size")
    val size: String
)