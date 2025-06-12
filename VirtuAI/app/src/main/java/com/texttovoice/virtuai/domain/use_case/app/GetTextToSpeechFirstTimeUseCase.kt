package com.texttovoice.virtuai.domain.use_case.app


import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetTextToSpeechFirstTimeUseCase @Inject constructor(private val preferenceRepository: PreferenceRepository) {
    operator fun invoke(): Boolean {
        return preferenceRepository.getTextToSpeechFirstTime()
    }
}