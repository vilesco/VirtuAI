package com.texttovoice.virtuai.domain.use_case.message


import com.texttovoice.virtuai.data.model.GPTModel
import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetSelectedGptUseCase @Inject constructor(private val preferenceRepository: PreferenceRepository) {
    operator fun invoke(): GPTModel {
        return preferenceRepository.getSelectedGpt()
    }
}