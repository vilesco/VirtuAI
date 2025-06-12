package com.texttovoice.virtuai.ui.speech

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.RequestBodySpeech
import com.texttovoice.virtuai.domain.use_case.audio.CreateSpeechUseCase
import com.texttovoice.virtuai.domain.use_case.language.GetCurrentLanguageCodeUseCase
import com.texttovoice.virtuai.domain.use_case.message.GetFreeMessageCountUseCase
import com.texttovoice.virtuai.domain.use_case.message.SetFreeMessageCountUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.IsProVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import javax.inject.Inject


@HiltViewModel
class TextToSpeechViewModel @Inject constructor(
    private val createSpeechUseCase: CreateSpeechUseCase,
    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
    private val isProVersionUseCase: IsProVersionUseCase,
    private val getFreeMessageCountUseCase: GetFreeMessageCountUseCase,
    private val setFreeMessageCountUseCase: SetFreeMessageCountUseCase,
) : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null


    private fun prepareAudio(context: Context, audio: Int) {
        mediaPlayer = MediaPlayer.create(context, audio)
        runBlocking {
            mediaPlayer?.setOnCompletionListener {
                playingVoice.value = 0
            }
        }


    }

    fun playPauseAudio(context: Context, audio: Int) {

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                if (playingVoice.value == audio) {
                    playingVoice.value = 0
                } else {
                    prepareAudio(context, audio)
                    playingVoice.value = audio
                    mediaPlayer?.start()
                }


            } else {
                playingVoice.value = audio
                it.start()
            }
        } ?: kotlin.run {
            prepareAudio(context, audio)
            playingVoice.value = audio
            mediaPlayer?.start()
        }
    }

    public override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        playingVoice.value = 0
    }


    val selectedValue = mutableStateOf("")
    val playingVoice = mutableStateOf(0)
    val selectedVoiceImage = mutableStateOf(0)
    val currentLanguageCode = mutableStateOf("en")

    private val _state = MutableSharedFlow<Resource<ResponseBody>?>()
    val state = _state.asSharedFlow()

    private val _freeMessageCount = MutableStateFlow(3)
    val freeMessageCount get() = _freeMessageCount.asStateFlow()

    private val _isProVersion = MutableStateFlow(false)
    val isProVersion get() = _isProVersion.asStateFlow()
    val showAdsAndProVersion = mutableStateOf(false)


    fun createSpeech(context: Context, input: String) = viewModelScope.launch {

        createSpeechUseCase(
            RequestBodySpeech(
                "tts-1",
                input,
                selectedValue.value
            )
        ).collect { resource ->
            _state.emit(resource)

        }

    }


    fun getCurrentLanguageCode() = viewModelScope.launch {
        currentLanguageCode.value = getCurrentLanguageCodeUseCase()
    }


    fun getProVersion() = viewModelScope.launch {
        _isProVersion.value = isProVersionUseCase()
        if (_isProVersion.value) {
            showAdsAndProVersion.value = false
        }
    }

    fun getFreeMessageCount() = viewModelScope.launch {

        _freeMessageCount.value = getFreeMessageCountUseCase()
        if (_freeMessageCount.value > 0) {
            showAdsAndProVersion.value = false
        }
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

}