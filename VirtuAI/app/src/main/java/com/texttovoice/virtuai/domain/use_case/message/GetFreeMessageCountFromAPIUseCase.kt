package com.texttovoice.virtuai.domain.use_case.message

import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetFreeMessageCountFromAPIUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke() = preferenceRepository.getFreeMessageCountFromAPI()
}