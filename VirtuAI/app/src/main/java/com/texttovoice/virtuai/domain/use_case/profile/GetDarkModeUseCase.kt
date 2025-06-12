package com.texttovoice.virtuai.domain.use_case.profile

import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke() = preferenceRepository.getDarkMode()
}