package com.texttovoice.virtuai.ui.activity

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.domain.use_case.language.GetCurrentLanguageCodeUseCase
import com.texttovoice.virtuai.domain.use_case.profile.GetDarkModeUseCase
import com.texttovoice.virtuai.domain.use_case.upgrade.SetFirstTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val getCurrentLanguageCodeUseCase: GetCurrentLanguageCodeUseCase,
    private val setFirstTimeUseCase: SetFirstTimeUseCase
) : ViewModel() {

    private val _darkMode = MutableStateFlow(true)
    val darkMode
        get() = _darkMode.asStateFlow()


    val currentLanguageCode = mutableStateOf("en")


    init {
        getDarkMode()
        setFirstTime()

    }


    private fun setFirstTime() = viewModelScope.launch {
        setFirstTimeUseCase(true)
    }

    private fun getDarkMode() = viewModelScope.launch {
        _darkMode.value = getDarkModeUseCase()
    }

    fun getCurrentLanguageCode() = viewModelScope.launch {
        currentLanguageCode.value = getCurrentLanguageCodeUseCase()
    }

}