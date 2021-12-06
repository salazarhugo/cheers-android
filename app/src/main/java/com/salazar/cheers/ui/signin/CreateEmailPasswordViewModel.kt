package com.salazar.cheers.ui.signin

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.Result
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateEmailPasswordViewModelState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean? = null,
    val errorMessage: String = "",
    val email: String = "",
    val password: String = "",
)

@HiltViewModel
class CreateEmailPasswordViewModel @Inject constructor(): ViewModel() {

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