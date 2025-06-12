package com.texttovoice.virtuai.ui.chat

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Constants.DEFAULT_AI
import com.texttovoice.virtuai.data.model.*
import com.texttovoice.virtuai.domain.use_case.app.GetTextToSpeechFirstTimeUseCase
import com.texttovoice.virtuai.domain.use_case.app.GetTextToSpeechUseCase
import com.texttovoice.virtuai.domain.use_case.app.SetTextToSpeechFirstTimeUseCase
import com.texttovoice.virtuai.domain.use_case.app.SetTextToSpeechUseCase
import com.texttovoice.virtuai.domain.use_case.conversation.CreateConversationUseCase
import com.texttovoice.virtuai.domain.use_case.conversation.GetConversationUseCase
import com.texttovoice.virtuai.domain.use_case.message.*
import com.texttovoice.virtuai.domain.use_case.upgrade.IsProVersionUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.SetProVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val textCompletionsWithStreamUseCase: TextCompletionsWithStreamUseCase,
    private val createMessagesUseCase: CreateMessagesUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createConversationUseCase: CreateConversationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val isProVersionUseCase: IsProVersionUseCase,
    private val getFreeMessageCountUseCase: GetFreeMessageCountUseCase,
    private val setFreeMessageCountUseCase: SetFreeMessageCountUseCase,
    private val setProVersionUseCase: SetProVersionUseCase,
    private val getTextToSpeechUseCase: GetTextToSpeechUseCase,
    private val setTextToSpeechUseCase: SetTextToSpeechUseCase,
    private val getTextToSpeechFirstTimeUseCase: GetTextToSpeechFirstTimeUseCase,
    private val setTextToSpeechFirstTimeUseCase: SetTextToSpeechFirstTimeUseCase,
    private val getSelectedGptUseCase: GetSelectedGptUseCase,
    private val setSelectedGptUseCase: SetSelectedGptUseCase,
    private val getConversationUseCase: GetConversationUseCase,
    private val application: Application
) : ViewModel() {
    var tts: TextToSpeech? = null

    private var answerFromGPT = ""
    private var newMessageModel = MessageModel()

    private val cScope = CoroutineScope(Dispatchers.IO)
    var job: Job? = null

    private val _currentConversation: MutableStateFlow<String> =
        MutableStateFlow(Date().time.toString())

    private val _messages: MutableStateFlow<HashMap<String, MutableList<MessageModel>>> =
        MutableStateFlow(HashMap())

    private val _isGenerating: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val currentConversationState: StateFlow<String> = _currentConversation.asStateFlow()
    val messagesState: StateFlow<HashMap<String, MutableList<MessageModel>>> =
        _messages.asStateFlow()
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()


    private val _isProVersion = MutableStateFlow(false)
    val isProVersion get() = _isProVersion.asStateFlow()

    private val _freeMessageCount = MutableStateFlow(3)
    val freeMessageCount get() = _freeMessageCount.asStateFlow()

//    private val _selectedGPT = MutableStateFlow(GPTModel.gpt35Turbo)
//    val selectedGPT get() = _selectedGPT.asStateFlow()


    val showAdsAndProVersion = mutableStateOf(false)
    val textToSpeech = mutableStateOf(false)
    private val textToSpeechCurrent = mutableStateOf(false)
    private val textToSpeechFirstTime = mutableStateOf(true)
    val selectedGPT = mutableStateOf(GPTModel.gpt35Turbo)
    val selectedGPTForTitle = mutableStateOf(GPTModel.gpt35Turbo)




    init {
        _currentConversation.value = savedStateHandle.get<String>("id")
            ?: Date().time.toString()



        viewModelScope.launch { fetchMessages() }
        textToSpeech.value = getTextToSpeechUseCase()
        textToSpeechFirstTime.value = getTextToSpeechFirstTimeUseCase()
        getGPTModel()
    }

    private suspend fun getConversation(conversationId: String): ConversationModel {
        return getConversationUseCase(conversationId)
    }

    fun getGPTModel() = viewModelScope.launch {
        val conversation = getConversation(_currentConversation.value)
        if (conversation != null) {
            selectedGPT.value = conversation.model
            selectedGPTForTitle.value = conversation.model
        }

    }

    fun setGPTModel(gpt35Turbo: GPTModel) {
//        setSelectedGptUseCase(gpt35Turbo)
        selectedGPT.value = gpt35Turbo
        selectedGPTForTitle.value = gpt35Turbo
    }


    fun toggleTextToSpeech() {

//        if (textToSpeechFirstTime.value) {
//            setTextToSpeechFirstTimeUseCase(false)
//            textToSpeechFirstTime.value = false
//        }

        textToSpeech.value = !textToSpeech.value
        setTextToSpeechUseCase(textToSpeech.value)

        if (!textToSpeech.value) {
            stopSpeech()
        }
    }

    fun stopSpeech() {
        tts?.stop()
        tts?.shutdown()
    }

    fun setProVersion(isPro: Boolean) = viewModelScope.launch {
        setProVersionUseCase(isPro)
        _isProVersion.value = isPro
        showAdsAndProVersion.value = false
    }

    fun getProVersion() = viewModelScope.launch {
        _isProVersion.value = isProVersionUseCase()
        if (_isProVersion.value) {
            showAdsAndProVersion.value = false
        } else {
            selectedGPT.value = GPTModel.gpt35Turbo
        }
    }

    fun getFreeMessageCount() = viewModelScope.launch {
        _freeMessageCount.value = getFreeMessageCountUseCase()
        Log.e("freeMessageCount", _freeMessageCount.value.toString())
    }

    fun decreaseFreeMessageCount() {
        viewModelScope.launch {
            _freeMessageCount.value = _freeMessageCount.value - 1
            setFreeMessageCountUseCase(_freeMessageCount.value)
        }
    }

    fun increaseFreeMessageCount() {
        viewModelScope.launch {
            _freeMessageCount.value =
                _freeMessageCount.value + Constants.Preferences.INCREASE_MESSAGE_COUNT
            setFreeMessageCountUseCase(_freeMessageCount.value)
        }
    }


    fun regenerateAnswer() {
        updateLocalAnswer("...")
        textToSpeechCurrent.value = textToSpeech.value

        // Execute API OpenAI
        val flow: Flow<String> = textCompletionsWithStreamUseCase(
            scope = cScope,
            TextCompletionsParam(
                promptText = getPrompt(_currentConversation.value),
                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value),
                model = selectedGPT.value
            )
        )


        answerFromGPT = ""
        val wordList: MutableList<String> = mutableListOf()
        job = cScope.launch {
            _isGenerating.value = true
            flow.collect {
                answerFromGPT += it
                wordList.add(it)

                if (!textToSpeechCurrent.value) {
                    updateLocalAnswer(answerFromGPT.trim())
                }
            }
            var answerFromGPTSeparated = ""

            if (textToSpeechCurrent.value) {
                tts = TextToSpeech(
                    application
                ) {
                    if (it == TextToSpeech.SUCCESS) {
                        tts?.let { txtToSpeech ->
                            txtToSpeech.language = Locale.ROOT
                            txtToSpeech.setPitch(1f)
                            txtToSpeech.setSpeechRate(1f)
                            txtToSpeech.speak(
                                answerFromGPT,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                null
                            )
                        }
                    }
                }

                wordList.forEach {
                    answerFromGPTSeparated += it
                    updateLocalAnswer(answerFromGPTSeparated.trim())
                    delay(100)
                }


            }


            // Save to Firestore
            createMessagesUseCase(newMessageModel.copy(answer = answerFromGPT))
            _isGenerating.value = false
            decreaseFreeMessageCount()
        }
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            createConversationRemote(message)
        }
        textToSpeechCurrent.value = textToSpeech.value

        newMessageModel = MessageModel(
            question = message,
            answer = "...",
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)


        // Execute API OpenAI
        val flow: Flow<String> = textCompletionsWithStreamUseCase(
            scope = cScope,
            TextCompletionsParam(
                promptText = getPrompt(_currentConversation.value),
                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value),
                model = selectedGPT.value
            )
        )


        answerFromGPT = ""
        val wordList: MutableList<String> = mutableListOf()
        job = cScope.launch {
            _isGenerating.value = true
            flow.collect {
                answerFromGPT += it
                wordList.add(it)

                if (!textToSpeechCurrent.value) {
                    updateLocalAnswer(answerFromGPT.trim())
                }
            }
            var answerFromGPTSeparated = ""

            if (textToSpeechCurrent.value) {
                tts = TextToSpeech(
                    application
                ) {
                    if (it == TextToSpeech.SUCCESS) {
                        tts?.let { txtToSpeech ->
                            txtToSpeech.language = Locale.ROOT
                            txtToSpeech.setPitch(1f)
                            txtToSpeech.setSpeechRate(1f)
                            txtToSpeech.speak(
                                answerFromGPT,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                null
                            )
                        }
                    }
                }

                wordList.forEach {
                    answerFromGPTSeparated += it
                    updateLocalAnswer(answerFromGPTSeparated.trim())
                    delay(100)
                }


            }


            // Save to Firestore
            createMessagesUseCase(newMessageModel.copy(answer = answerFromGPT))
            _isGenerating.value = false
        }


    }

    fun stopGenerate() = viewModelScope.launch {
        job?.cancel()
        _isGenerating.value = false
        createMessagesUseCase(newMessageModel.copy(answer = answerFromGPT))
    }

    private fun createConversationRemote(title: String) = viewModelScope.launch {
        val newConversation = ConversationModel(
            id = _currentConversation.value,
            title = title,
            model = selectedGPT.value,
            createdAt = Calendar.getInstance().time.toString()
        )

        createConversationUseCase(newConversation)
    }

    private fun getMessagesByConversation(conversationId: String): MutableList<MessageModel> {
        if (_messages.value[conversationId] == null) return mutableListOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        return messagesMap[conversationId]!!
    }

    private fun getPrompt(conversationId: String): String {
        if (_messages.value[conversationId] == null) return ""

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        var response: String = ""

        for (message in messagesMap[conversationId]!!.reversed()) {
            response += """
            Human:${message.question.trim()}
            Bot:${if (message.answer == "...") "" else message.answer.trim()}"""
        }

        return response
    }

    private fun getMessagesParamsTurbo(conversationId: String): List<MessageTurbo> {
        if (_messages.value[conversationId] == null) return listOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        val role: String = checkNotNull(savedStateHandle["role"])


        val response: MutableList<MessageTurbo> = mutableListOf(
            MessageTurbo(
                role = TurboRole.system,
                content = "$DEFAULT_AI $role"
            )
        )

        for (message in messagesMap[conversationId]!!.reversed()) {
            response.add(MessageTurbo(content = message.question))

            if (message.answer != "...") {
                response.add(MessageTurbo(content = message.answer, role = TurboRole.user))
            }
        }

        return response.toList()
    }

    private suspend fun fetchMessages() {
        if (_currentConversation.value.isEmpty() || _messages.value[_currentConversation.value] != null) return

        val list: List<MessageModel> = getMessagesUseCase(_currentConversation.value)

        setMessages(list.toMutableList())

    }

    private fun updateLocalAnswer(answer: String) {
        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage[0] = currentListMessage[0].copy(answer = answer)

        setMessages(currentListMessage)
    }

    private fun setMessages(messages: MutableList<MessageModel>) {
        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        messagesMap[_currentConversation.value] = messages

        _messages.value = messagesMap
    }


}