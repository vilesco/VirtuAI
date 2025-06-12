package com.texttovoice.virtuai.ui.startchat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.common.Constants.Preferences.FREE_MESSAGE_COUNT_DEFAULT
import com.texttovoice.virtuai.domain.use_case.app.IsThereUpdateUseCase
import com.texttovoice.virtuai.domain.use_case.language.GetCurrentLanguageCodeUseCase
import com.texttovoice.virtuai.domain.use_case.message.GetFreeMessageCountFromAPIUseCase
import com.texttovoice.virtuai.domain.use_case.message.GetFreeMessageCountUseCase
import com.texttovoice.virtuai.domain.use_case.message.SetFreeMessageCountUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.IsFirstTimeUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.IsProVersionFromAPIUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.IsProVersionUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.SetFirstTimeUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.SetProVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartChatViewModel @Inject constructor(
    private val isProVersionUseCase: IsProVersionUseCase,
    private val isProVersionFromAPIUseCase: IsProVersionFromAPIUseCase,
    private val setProVersionUseCase: SetProVersionUseCase,
    private val isFirstTimeUseCase: IsFirstTimeUseCase,
    private val setFirstTimeUseCase: SetFirstTimeUseCase,
    private val isThereUpdateUseCase: IsThereUpdateUseCase,
    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
    private val getFreeMessageCountUseCase: GetFreeMessageCountUseCase,
    private val getFreeMessageCountFromAPIUseCase: GetFreeMessageCountFromAPIUseCase,
    private val setFreeMessageCountUseCase: SetFreeMessageCountUseCase
) :
    ViewModel() {

    val isProVersion = mutableStateOf(false)
    val isFirstTime = mutableStateOf(true)
    val isThereUpdate = mutableStateOf(false)
    val currentLanguageCode = mutableStateOf("en")
    private val _freeMessageCount = MutableStateFlow(FREE_MESSAGE_COUNT_DEFAULT)
    val freeMessageCount get() = _freeMessageCount.asStateFlow()

    init {
        getCurrentLanguageCode()
    }


    fun getFreeMessageCount() = viewModelScope.launch {
        _freeMessageCount.value = getFreeMessageCountUseCase()
        Log.e("freeMessageCount", _freeMessageCount.value.toString())
    }

    fun getFreeMessageCountFromAPI() = viewModelScope.launch {
        _freeMessageCount.value = getFreeMessageCountFromAPIUseCase()
        setFreeMessageCountUseCase(_freeMessageCount.value)
    }

    fun isThereUpdate() = viewModelScope.launch {
        isThereUpdate.value = isThereUpdateUseCase()
    }

    fun getProVersion() = viewModelScope.launch {
        isProVersion.value = isProVersionUseCase()
        Log.e("getProVersion", isProVersion.value.toString())

    }

    fun getProVersionFromAPI() = viewModelScope.launch {
        isProVersion.value = isProVersionFromAPIUseCase()
        setProVersionUseCase(isProVersion.value)

    }

    fun getFirstTime() = viewModelScope.launch {
        isFirstTime.value = isFirstTimeUseCase()
    }

    fun setProVersion(isPro: Boolean) = viewModelScope.launch {
        setProVersionUseCase(isPro)
        isProVersion.value = isPro
    }

    fun setFirstTime(isFirstTime: Boolean) = setFirstTimeUseCase(isFirstTime)


    fun getCurrentLanguageCode() = viewModelScope.launch {
        currentLanguageCode.value = getCurrentLanguageCodeUseCase()
    }
}