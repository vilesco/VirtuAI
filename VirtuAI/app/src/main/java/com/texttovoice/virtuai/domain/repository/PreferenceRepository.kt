package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.data.model.GPTModel
import com.texttovoice.virtuai.data.model.User

interface PreferenceRepository {
    fun setDarkMode(isDarkMode: Boolean)
    fun getDarkMode(): Boolean
    fun setCurrentLanguage(language: String)
    fun getCurrentLanguage(): String
    fun setCurrentLanguageCode(language: String)
    fun getCurrentLanguageCode(): String
    suspend fun isProVersion(): Boolean
    suspend fun isProVersionFromAPI(): Boolean
    suspend fun setProVersion(isProVersion: Boolean)
    fun isFirstTime(): Boolean
    fun setFirstTime(isFirstTime: Boolean)
    suspend fun getFreeMessageCount(): Int
    suspend fun getFreeMessageCountFromAPI(): Int
    suspend fun setFreeMessageCount(count: Int)
    suspend fun saveUser(user: User)
    fun getTextToSpeech(): Boolean
    fun setTextToSpeech(isEnabled : Boolean)
    fun getTextToSpeechFirstTime(): Boolean
    fun setTextToSpeechFirstTime(isEnabled : Boolean)
    fun getSelectedGpt(): GPTModel
    fun setSelectedGpt(gptModel : GPTModel)
}