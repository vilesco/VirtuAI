package com.texttovoice.virtuai.data.repository

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Constants.Preferences.FREE_MESSAGE_COUNT_DEFAULT
import com.texttovoice.virtuai.data.model.GPTModel
import com.texttovoice.virtuai.data.model.User
import com.texttovoice.virtuai.domain.repository.PreferenceRepository
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firestore: FirebaseFirestore,
    private val app: Application
) : PreferenceRepository {
    override fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.Preferences.DARK_MODE, isDarkMode).apply()
    }

    override fun getDarkMode(): Boolean {
        return sharedPreferences.getBoolean(
            Constants.Preferences.DARK_MODE,
            true
        )
    }

    override fun setCurrentLanguage(language: String) {
        sharedPreferences.edit().putString(Constants.Preferences.LANGUAGE_NAME, language).apply()
    }

    override fun getCurrentLanguage(): String =
        sharedPreferences.getString(
            Constants.Preferences.LANGUAGE_NAME,
            Locale.getDefault().displayLanguage
        ) ?: Locale.getDefault().displayLanguage

    override fun setCurrentLanguageCode(language: String) {
        sharedPreferences.edit().putString(Constants.Preferences.LANGUAGE_CODE, language).apply()
    }

    override fun getCurrentLanguageCode(): String =
        sharedPreferences.getString(
            Constants.Preferences.LANGUAGE_CODE,
            Locale.getDefault().language
        ) ?: Locale.getDefault().language

    override suspend fun isProVersion(): Boolean =
        sharedPreferences.getBoolean(
            Constants.Preferences.PRO_VERSION,
            false
        )

    override suspend fun isProVersionFromAPI(): Boolean {
        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                FirebaseAuth.getInstance().currentUser?.email
            ) // Query by email or other unique identifier
            val snapshot = query.get().await()

            return if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                val user = userDoc.toObject(User::class.java)

                // Check if the user object is not null and return the isProUser value
                user?.isProUser ?: false
            } else {
                saveUser(
                    User(
                        "",
                        FirebaseAuth.getInstance().currentUser?.email ?: "",
                        false
                    )
                )
                false // Return false if the user is not found
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        }

    }


    override suspend fun setProVersion(isProVersion: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.Preferences.PRO_VERSION, isProVersion)
            .apply()

        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                FirebaseAuth.getInstance().currentUser?.email
            ) // You can use another unique identifier
            val snapshot = query.get().await()

            if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                userDoc.reference.update("isProUser", isProVersion)
                Log.d("FirebaseRepository", "User updated successfully")
            } else {
                saveUser(
                    User(
                        "",
                        FirebaseAuth.getInstance().currentUser?.email ?: "",
                        isProVersion
                    )
                )
                Log.d("FirebaseRepository", "User not found for updating")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    override fun isFirstTime(): Boolean =
        sharedPreferences.getBoolean(
            Constants.Preferences.FIRST_TIME,
            true
        )

    override fun setFirstTime(isFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.Preferences.FIRST_TIME, isFirstTime)
            .apply()
    }

    override suspend fun getFreeMessageCount(): Int {
        // Get the saved free message count and last checked time from SharedPreferences.
        //        val lastCheckedTime = sharedPreferences.getLong(
//            Constants.Preferences.FREE_MESSAGE_LAST_CHECKED_TIME,
//            0L
//        )
//
//        // Check if the last checked time was yesterday or earlier.
//        val currentTime = System.currentTimeMillis()
//        val lastCheckedCalendar = Calendar.getInstance().apply { timeInMillis = lastCheckedTime }
//        val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTime }
//        if (lastCheckedCalendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR) ||
//            lastCheckedCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)
//        ) {
//            // If last checked time was yesterday or earlier, reset the free message count to 3.
//            sharedPreferences.edit()
//                .putInt(Constants.Preferences.FREE_MESSAGE_COUNT, FREE_MESSAGE_COUNT_DEFAULT)
//                .putLong(Constants.Preferences.FREE_MESSAGE_LAST_CHECKED_TIME, currentTime)
//                .apply()
//            return FREE_MESSAGE_COUNT_DEFAULT
//        }
        return sharedPreferences.getInt(
            Constants.Preferences.FREE_MESSAGE_COUNT,
            FREE_MESSAGE_COUNT_DEFAULT
        )


    }

    override suspend fun getFreeMessageCountFromAPI(): Int {
        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                FirebaseAuth.getInstance().currentUser?.email
            ) // Query by email or other unique identifier
            val snapshot = query.get().await()

            return if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                val user = userDoc.toObject(User::class.java)

                // Check if the user object is not null and return the isProUser value
                user?.remainingMessageCount ?: FREE_MESSAGE_COUNT_DEFAULT
            } else {
                saveUser(
                    User(
                        "",
                        FirebaseAuth.getInstance().currentUser?.email ?: "",
                        false
                    )
                )

                FREE_MESSAGE_COUNT_DEFAULT // Return false if the user is not found
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        }


    }

    override suspend fun setFreeMessageCount(count: Int) {
        sharedPreferences.edit().putInt(Constants.Preferences.FREE_MESSAGE_COUNT, count)
            .apply()

        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                FirebaseAuth.getInstance().currentUser?.email
            ) // You can use another unique identifier
            val snapshot = query.get().await()

            if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                userDoc.reference.update("remainingMessageCount", count)
                Log.d("FirebaseRepository", "User updated successfully")
            } else {
                saveUser(
                    User(
                        "",
                        FirebaseAuth.getInstance().currentUser?.email ?: "",
                        false,
                        count
                    )
                )
                Log.d("FirebaseRepository", "User not found for updating")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    override suspend fun saveUser(user: User) {
        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                user.email
            )  // Assuming email is a unique identifier for User
            val snapshot = query.get().await()

            if (snapshot.isEmpty) {
                // User does not exist, add them to the database
                val newUserRef = usersCollection.add(user).await()
                newUserRef.update("id", newUserRef.id).await()
                Log.d("FirebaseRepository", "User saved successfully with ID: ${newUserRef.id}")
            } else {
                // User already exists
                Log.d("FirebaseRepository", "User already exists")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    override fun getTextToSpeech(): Boolean {
        return sharedPreferences.getBoolean(
            Constants.Preferences.TEXT_TO_SPEECH,
            false
        )
    }

    override fun setTextToSpeech(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.Preferences.TEXT_TO_SPEECH, isEnabled).apply()
    }


    override fun getTextToSpeechFirstTime(): Boolean {
        return sharedPreferences.getBoolean(
            Constants.Preferences.TEXT_TO_SPEECH_FIRST_TIME,
            true
        )
    }

    override fun setTextToSpeechFirstTime(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(Constants.Preferences.TEXT_TO_SPEECH_FIRST_TIME, isEnabled).apply()
    }

    override fun getSelectedGpt(): GPTModel {

        val gptVersion = sharedPreferences.getString(
            Constants.Preferences.GPT_MODEL, Constants.DEFAULT_GPT_MODEL
        )

        return when (gptVersion) {
            "3.5" -> GPTModel.gpt35Turbo
            "4" -> GPTModel.gpt4
            else -> GPTModel.gpt35Turbo
        }

    }

    override fun setSelectedGpt(gptModel: GPTModel) {

        val gptVersion = when (gptModel) {
            GPTModel.gpt35Turbo -> "3.5"
            GPTModel.gpt4 -> "4"
            else -> "3.5"
        }

        sharedPreferences.edit()
            .putString(Constants.Preferences.GPT_MODEL, gptVersion).apply()

    }


}