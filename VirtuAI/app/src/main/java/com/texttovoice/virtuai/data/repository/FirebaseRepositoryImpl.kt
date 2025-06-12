package com.texttovoice.virtuai.data.repository

import android.app.Application
import android.content.pm.PackageInfo
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.texttovoice.virtuai.data.model.User
import com.texttovoice.virtuai.domain.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val app: Application
) :
    FirebaseRepository {
    override suspend fun isThereUpdate(): Boolean {
        // Uncomment this block to enable Firebase Remote Config

        return try {
            val pInfo: PackageInfo =
                app.packageManager.getPackageInfo(app.packageName, 0)
            val current_version = pInfo.versionCode
            Log.e("version_code", current_version.toString())

            val version = firestore.collection("app").document("app_info")
                .get().await()
            val versionCode = version.data?.get("app_version_code").toString().toInt()
            Log.e("version_code", versionCode.toString())

            versionCode != current_version

        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            false
        }

    }


    override suspend fun getTheApiKey(): String {

        return try {
            val version = firestore.collection("app").document("app_info")
                .get().await()
            val apiKey = version.data?.get("api_key").toString()

            apiKey

        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            ""
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
                usersCollection.document(newUserRef.id).update("id", newUserRef.id).await()
                Log.d("FirebaseRepository", "User saved successfully with ID: ${newUserRef.id}")
            } else {
                // User already exists
                Log.d("FirebaseRepository", "User already exists")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }


    override suspend fun updateUser(user: User) {
        try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                user.email
            ) // You can use another unique identifier
            val snapshot = query.get().await()

            if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                userDoc.reference.update("remainingMessageCount", user.remainingMessageCount)
                userDoc.reference.update("isProUser", user.isProUser)
                Log.d("FirebaseRepository", "User updated successfully")
            } else {
                Log.d("FirebaseRepository", "User not found for updating")
                saveUser(user)
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    override suspend fun getUser(email: String): User {
        return try {
            val usersCollection = firestore.collection("users")
            val query = usersCollection.whereEqualTo(
                "email",
                email
            ) // Query by email or other unique identifier
            val snapshot = query.get().await()

            if (!snapshot.isEmpty) {
                val userDoc = snapshot.documents.first()
                val user = userDoc.toObject(User::class.java)
                user ?: throw RuntimeException("User document does not contain valid user data")
            } else {
                saveUser(User("", email, false))
                throw RuntimeException("User not found")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        }
    }
}