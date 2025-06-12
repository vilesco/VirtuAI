package com.texttovoice.virtuai.ui.image

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.common.Constants.SIZE_1024
import com.texttovoice.virtuai.common.Constants.SIZE_256
import com.texttovoice.virtuai.common.Constants.SIZE_512
import com.texttovoice.virtuai.common.Resource
import com.texttovoice.virtuai.data.model.GeneratedImage
import com.texttovoice.virtuai.data.model.RequestBody
import com.texttovoice.virtuai.data.model.Sizes
import com.texttovoice.virtuai.domain.use_case.image.GenerateImageUseCase
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
import javax.inject.Inject

@HiltViewModel
class GenerateImageViewModel @Inject constructor(
    private val generateImageUseCase: GenerateImageUseCase,
    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
    private val isProVersionUseCase: IsProVersionUseCase,
    private val getFreeMessageCountUseCase: GetFreeMessageCountUseCase,
    private val setFreeMessageCountUseCase: SetFreeMessageCountUseCase,
) : ViewModel() {

    val selectedValue = mutableStateOf("")
    val selectedPrompt = mutableStateOf("")
    val currentLanguageCode = mutableStateOf("en")

    private val _state = MutableSharedFlow<Resource<GeneratedImage>?>()
    val state = _state.asSharedFlow()

    private val _freeMessageCount = MutableStateFlow(3)
    val freeMessageCount get() = _freeMessageCount.asStateFlow()

    private val _isProVersion = MutableStateFlow(false)
    val isProVersion get() = _isProVersion.asStateFlow()
    val showAdsAndProVersion = mutableStateOf(false)


    fun generateImage(prompt: String, n: Int, size: Sizes) = viewModelScope.launch {

        val sizeString = when (size) {
            Sizes.SIZE_256 -> SIZE_256
            Sizes.SIZE_512 -> SIZE_512
            Sizes.SIZE_1024 -> SIZE_1024
        }

        generateImageUseCase(RequestBody("dall-e-3", n, prompt, sizeString)).collect {
            _state.emit(it)
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