package com.texttovoice.virtuai.domain.use_case.app

import com.texttovoice.virtuai.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetApiKeyUseCase @Inject constructor(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(): String {
        return firebaseRepository.getTheApiKey()
    }
}