package com.texttovoice.virtuai.domain.use_case.audio

import com.texttovoice.virtuai.data.model.RequestBodySpeech
import com.texttovoice.virtuai.domain.repository.AudioRepository
import javax.inject.Inject

class CreateSpeechUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend  operator fun invoke(requestBody: RequestBodySpeech) = audioRepository.createSpeech(requestBody)
}