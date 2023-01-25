package com.salazar.cheers.auth.ui.register

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.auth.domain.usecase.RegisterUseCase
import com.salazar.cheers.auth.domain.usecase.SignInUseCase
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.util.Utils.validateUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
    val termsAccepted: Boolean = false,
    val success: Boolean = false,
    val isUsernameAvailable: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storeUserEmail: StoreUserEmail,
    private val userRepository: UserRepository,
    private val registerUseCase: RegisterUseCase,
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RegisterUiState(isLoading = false))
    lateinit var emailLink: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    init {
        stateHandle.get<String>("emailLink")?.let {
            emailLink = it
        }
    }

    fun onClearUsername() {
        viewModelState.update {
            it.copy(username = "")
        }
    }

    fun onUsernameChanged(username: String) {
        viewModelState.update {
            it.copy(username = username)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onAcceptTermsChange(accepted: Boolean) {
        viewModelState.update {
            it.copy(termsAccepted = accepted)
        }
    }

    private fun updateErrorMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateUsernameAvailable(available: Boolean) {
        viewModelState.update {
            it.copy(isUsernameAvailable = available)
        }
    }

    fun checkUsername() {
        val username = uiState.value.username
        updateIsLoading(true)

        if (!username.validateUsername()) {
            updateErrorMessage("Usernames can only include lowercase letters, numbers, underscores and full stops.")
            updateIsLoading(false)
            return
        }

        isUsernameAvailable(username) { result ->
            updateIsLoading(false)
            if (!result) {
                updateErrorMessage("This username is taken")
                return@isUsernameAvailable
            }
            updateUsernameAvailable(result)
        }
    }

    private fun isUsernameAvailable(
        username: String,
        onResponse: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val res = userRepository.isUsernameAvailable(username = username)
            onResponse(res)
        }
    }

    fun registerUser() {
        updateIsLoading(true)
        val username = uiState.value.username

        viewModelScope.launch {
            val signInResult = signInUseCase(
                emailLink = emailLink,
            )

            if (signInResult is Resource.Error) {
                updateIsLoading(false)
                return@launch
            }

            val result = registerUseCase(
                username = username,
            )
            when(result) {
                is Resource.Error -> updateErrorMessage(result.message)
                is Resource.Loading -> updateIsLoading(result.isLoading)
                is Resource.Success -> viewModelState.update { it.copy(success = true) }
            }
            updateIsLoading(false)
        }
    }
}

