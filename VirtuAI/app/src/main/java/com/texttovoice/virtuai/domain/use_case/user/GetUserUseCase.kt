package com.texttovoice.virtuai.domain.use_case.user


import com.texttovoice.virtuai.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(email: String) =
        firebaseRepository.getUser(email)
}