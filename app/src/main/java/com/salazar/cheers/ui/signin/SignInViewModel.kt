package com.salazar.cheers.ui.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    fun onPasswordChange(password: String) {
        this.password.value = password
    }

    fun onEmailChange(email: String) {
        this.email.value = email
    }
}