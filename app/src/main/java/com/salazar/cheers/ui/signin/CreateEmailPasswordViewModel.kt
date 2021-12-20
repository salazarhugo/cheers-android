package com.salazar.cheers.ui.signin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CreateEmailPasswordViewModelState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean? = null,
    val errorMessage: String = "",
    val email: String = "",
    val password: String = "",
)

@HiltViewModel
class CreateEmailPasswordViewModel @Inject constructor() : ViewModel() {

    val uiState = MutableStateFlow(CreateEmailPasswordViewModelState(isLoading = false))

    fun clearEmail() {
        uiState.update {
            it.copy(email = "")
        }
    }

    fun onEmailChanged(email: String) {
        uiState.update {
            it.copy(email = email)
        }
    }

    fun onPasswordChanged(password: String) {
        uiState.update {
            it.copy(password = password)
        }
    }

    fun reset() {
        uiState.update {
            CreateEmailPasswordViewModelState()
        }
    }


}