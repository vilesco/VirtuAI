package com.texttovoice.virtuai.domain.use_case.upgrade

import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class IsFirstTimeUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke() = preferenceRepository.isFirstTime()
}