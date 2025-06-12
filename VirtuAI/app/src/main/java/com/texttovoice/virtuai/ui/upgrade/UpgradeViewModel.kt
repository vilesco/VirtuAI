package com.texttovoice.virtuai.ui.upgrade

import androidx.lifecycle.ViewModel
import com.texttovoice.virtuai.domain.use_case.upgrade.SetProVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpgradeViewModel @Inject constructor(private val setProVersionUseCase: SetProVersionUseCase) :
    ViewModel() {

    suspend fun setProVersion(isProVersion: Boolean) = setProVersionUseCase(isProVersion)
}