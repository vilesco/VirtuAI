package com.texttovoice.virtuai.domain.use_case.user

import com.texttovoice.virtuai.data.model.User
import com.texttovoice.virtuai.domain.repository.FirebaseRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(user: User) =
        firebaseRepository.updateUser(user)
}