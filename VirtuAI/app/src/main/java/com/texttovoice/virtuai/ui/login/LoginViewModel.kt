package com.texttovoice.virtuai.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttovoice.virtuai.data.model.User
import com.texttovoice.virtuai.domain.use_case.user.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val saveUserUseCase: SaveUserUseCase) :
    ViewModel() {

     fun saveUser(user: User) = viewModelScope.launch {
        saveUserUseCase(user)
    }

}