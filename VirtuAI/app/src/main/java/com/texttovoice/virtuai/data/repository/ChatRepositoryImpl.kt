package com.texttovoice.virtuai.data.repository

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.data.model.TextCompletionsParam
import com.texttovoice.virtuai.data.model.toJson
import com.texttovoice.virtuai.data.source.remote.ConversAIService
import com.texttovoice.virtuai.domain.repository.ChatRepository
import com.texttovoice.virtuai.domain.use_case.language.GetCurrentLanguageCodeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import com.texttovoice.virtuai.R

//class ChatRepositoryImpl @Inject constructor(
//    private val conversAIService: ConversAIService,
//    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
//    private val app: Application
//) : ChatRepository {
//
//    // Single Gson instance for reuse
//    private val gson by lazy { Gson() }
//
//    override fun textCompletionsWithStream(
//        scope: CoroutineScope,
//        params: TextCompletionsParam
//    ): Flow<String> = callbackFlow {
//        withContext(Dispatchers.IO) {
//            try {
//                changeLanguageIfNeeded(getCurrentLanguageCodeUseCase())
//
//                // Modify the params here if necessary
//
//                val inputJsonObject = JsonObject().apply {
//                    add("input", gson.toJsonTree(params.messagesTurbo.last().content))
//                }
//
//                val moderationsResponse = conversAIService.textModerations(inputJsonObject).execute()
//
//                if (moderationsResponse.isSuccessful) {
//                    moderationsResponse.body()?.let { body ->
//                        if (checkIfFlagged(body.string())) {
//                            trySend(app.getString(R.string.flagged_message_content))
//                        } else {
//                            val response = if (params.isTurbo) {
//                                conversAIService.textCompletionsTurboWithStream(params.toJson())
//                            } else {
//                                conversAIService.textCompletionsWithStream(params.toJson())
//                            }.execute()
//
//                            if (response.isSuccessful) {
//                                response.body()?.byteStream()?.bufferedReader().use { input ->
//                                    input?.forEachLine { line ->
//                                        val data = processDataLine(line, params.isTurbo)
//                                        if (data.isNotEmpty()) {
//                                            trySend(data)
//                                        }
//                                        if (line == "data: [DONE]") return@forEachLine
//                                    }
//                                }
//                            } else {
//                                response.errorBody()?.string()?.let { error ->
//                                    trySend(error)
//                                }
//                            }
//                        }
//                    } ?: moderationsResponse.errorBody()?.string()?.let { error ->
//                        trySend(error)
//                    }
//                } else {
//                    moderationsResponse.errorBody()?.string()?.let { error ->
//                        trySend(error)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                trySend(e.localizedMessage ?: "Error")
//            } finally {
//                close()
//            }
//        }
//    }.flowOn(Dispatchers.IO)
//
//    private fun processDataLine(line: String, isTurbo: Boolean): String {
//        return if (line.startsWith("data:")) {
//            if (isTurbo) lookupDataFromResponseTurbo(line) else lookupDataFromResponse(line)
//        } else ""
//    }
//
//    private fun lookupDataFromResponse(jsonString: String): String {
//        val regex = """"text"\s*:\s*"([^"]+)"""".toRegex()
//        val matchResult = regex.find(jsonString)
//
//        if (matchResult != null && matchResult.groupValues.size > 1) {
//            val extractedText = matchResult.groupValues[1]
//            return extractedText
//                .replace("\\n\\n", " ")
//                .replace("\\n", " ")
//        }
//
//        return " "
//    }
//
//    private fun lookupDataFromResponseTurbo(jsonString: String): String {
//        val regex = """"content"\s*:\s*"([^"]+)"""".toRegex()
//        val matchResult = regex.find(jsonString)
//
//        if (matchResult != null && matchResult.groupValues.size > 1) {
//            val extractedText = matchResult.groupValues[1]
//            return extractedText
//                .replace("\\n\\n", " ")
//                .replace("\\n", " ")
//        }
//
//        return " "
//    }
//
//    private fun checkIfFlagged(jsonString: String): Boolean {
//        try {
//            val jsonObject = JSONObject(jsonString)
//
//            val choicesArray = jsonObject.optJSONArray("results")
//            if (choicesArray != null && choicesArray.length() > 0) {
//                val choiceObject = choicesArray.optJSONObject(0)
//                if (choiceObject != null) {
//                    return choiceObject.optBoolean("flagged")
//                }
//
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        return false
//    }
//
//    private fun changeLanguageIfNeeded(newLanguageCode: String) {
//        val currentLanguage = Locale.getDefault().language
//        if (newLanguageCode != currentLanguage) {
//            val locale = Locale(newLanguageCode)
//            Locale.setDefault(locale)
//            val config = Configuration()
//            config.setLocale(locale)
//            app.resources.updateConfiguration(config, app.resources.displayMetrics)
//        }
//    }
//}


class ChatRepositoryImpl @Inject constructor(
    private val conversAIService: ConversAIService,
    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
    private val app: Application
) :
    ChatRepository {
    override fun textCompletionsWithStream(
        scope: CoroutineScope,
        params: TextCompletionsParam
    ): Flow<String> =
        callbackFlow {
            try {

                withContext(Dispatchers.IO) {

                    changeLanguage(app, getCurrentLanguageCodeUseCase())

                    if (params.messagesTurbo.last().content.startsWith(Constants.START_WEB_LINK)) {

                        val text = app.getString(
                            R.string.web_url_content,
                            params.messagesTurbo.last().content.split("||")[2]
                        )
                        params.messagesTurbo.last().content = text
                    }


                    val inoutJsonObject = JsonObject()
                    inoutJsonObject.add(
                        "input",
                        Gson().toJsonTree(params.messagesTurbo.last().content)
                    )

                    val moderationsResponse =
                        conversAIService.textModerations(inoutJsonObject).execute()

                    if (moderationsResponse.isSuccessful) {
                        moderationsResponse.body()?.let {
                            if (checkIfFlagged(it.string())) {
                                trySend(app.getString(R.string.flagged_message_content))
                                close()
                            } else {
                                val response =
                                    (if (params.isTurbo) conversAIService.textCompletionsTurboWithStream(
                                        params.toJson()
                                    ) else conversAIService.textCompletionsWithStream(params.toJson())).execute()

                                if (response.isSuccessful) {
                                    val input = response.body()?.byteStream()?.bufferedReader()
                                        ?: throw Exception()
                                    try {
                                        while (true) {
                                            val line = withContext(Dispatchers.IO) {
                                                input.readLine()
                                            } ?: continue
                                            if (line == "data: [DONE]") {
                                                close()
                                            } else if (line.startsWith("data:")) {
                                                try {
                                                    // Handle & convert data -> emit to client
                                                    val value =
                                                        if (params.isTurbo) lookupDataFromResponseTurbo(
                                                            line
                                                        ) else lookupDataFromResponse(
                                                            line
                                                        )

                                                    if (value.isNotEmpty()) {
                                                        trySend(value)
                                                    }
                                                } catch (e: Exception) {

                                                    e.printStackTrace()
                                                }
                                            }
                                            if (!scope.isActive) {
                                                break
                                            }
                                        }
                                    } catch (e: IOException) {
                                        throw Exception(e)
                                    } finally {
                                        withContext(Dispatchers.IO) {
                                            input.close()
                                        }

                                        close()
                                    }
                                } else {
                                    if (!response.isSuccessful) {
                                        var jsonObject: JSONObject? = null
                                        try {
                                            jsonObject = JSONObject(response.errorBody()!!.string())
                                            println(jsonObject)
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }
                                    trySend(response.errorBody()!!.string())
                                    close()
                                }
                            }
                        } ?: run {
                            trySend(moderationsResponse.errorBody()!!.string())
                            close()
                        }
                    } else {
                        trySend(moderationsResponse.errorBody()!!.string())
                        close()
                    }


                }

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(e.localizedMessage)
                close()
            } finally {
                close()
            }
        }.flowOn(Dispatchers.IO)

    private fun lookupDataFromResponse(jsonString: String): String {
        val regex = """"text"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(jsonString)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val extractedText = matchResult.groupValues[1]
            return extractedText
                .replace("\\n\\n", " ")
                .replace("\\n", " ")
        }

        return " "
    }

    private fun lookupDataFromResponseTurbo(jsonString: String): String {
        val regex = """"content"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(jsonString)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val extractedText = matchResult.groupValues[1]
            return extractedText
                .replace("\\n\\n", " ")
                .replace("\\n", " ")
        }

        return " "
    }

    private fun checkIfFlagged(jsonString: String): Boolean {
        try {
            val jsonObject = JSONObject(jsonString)

            val choicesArray = jsonObject.optJSONArray("results")
            if (choicesArray != null && choicesArray.length() > 0) {
                val choiceObject = choicesArray.optJSONObject(0)
                if (choiceObject != null) {
                    return choiceObject.optBoolean("flagged")
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return false
    }


    fun changeLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

    }
}


