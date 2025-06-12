package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.data.model.User

interface FirebaseRepository {
    suspend fun isThereUpdate(): Boolean
    suspend fun getTheApiKey(): String
    suspend fun saveUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun getUser(email: String): User
}