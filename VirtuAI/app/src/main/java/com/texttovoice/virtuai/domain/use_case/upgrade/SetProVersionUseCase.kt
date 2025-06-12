package com.texttovoice.virtuai.domain.use_case.upgrade

import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class SetProVersionUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(isProVersion: Boolean) = preferenceRepository.setProVersion(isProVersion)
}
