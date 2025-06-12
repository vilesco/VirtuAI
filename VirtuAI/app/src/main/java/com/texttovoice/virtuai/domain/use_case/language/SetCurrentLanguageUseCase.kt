package com.texttovoice.virtuai.domain.use_case.language

import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class SetCurrentLanguageUseCase @Inject constructor(private val preferenceRepository: PreferenceRepository) {
    operator fun invoke(language: String) = preferenceRepository.setCurrentLanguage(language)
}