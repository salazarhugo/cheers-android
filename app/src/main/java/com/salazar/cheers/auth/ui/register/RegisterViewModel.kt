package com.salazar.cheers.auth.ui.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.util.Utils.validateUsername
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.domain.register.RegisterUseCase
import com.salazar.cheers.domain.usecase.SignInWithEmailLinkUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val userRepositoryImpl: UserRepositoryImpl,
    private val registerUseCase: RegisterUseCase,
    private val signInWithEmailLinkUseCase: SignInWithEmailLinkUseCase,
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

        viewModelScope.launch {
            val signInResult = signInWithEmailLinkUseCase(
                emailLink = emailLink,
            )

            if (signInResult is Resource.Error) {
                updateIsLoading(false)
            }
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

    fun checkUsername(onComplete: (Boolean) -> Unit) {
        val username = uiState.value.username
        updateIsLoading(true)

        if (!username.validateUsername()) {
            updateErrorMessage("Usernames can only include lowercase letters, numbers, underscores and full stops.")
            updateIsLoading(false)
            return
        }

        viewModelScope.launch {
            val result = userRepositoryImpl.checkUsername(username = username)
            result.onSuccess { valid ->
                updateUsernameAvailable(valid)
                onComplete(valid)
                if (!valid)
                    updateErrorMessage("This username is taken")
            }
            updateIsLoading(false)
        }
    }

    fun registerUser() {
        updateIsLoading(true)
        val username = uiState.value.username

        viewModelScope.launch {
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

