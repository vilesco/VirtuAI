package com.texttovoice.virtuai.data.model

data class VoiceStyle(
    val voiceName: String,
    val image: Int,
    val voice: String = "",
    val voiceFile: Int
)
