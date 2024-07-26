package com.salazar.cheers.feature.signup

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.domain.RegisterPasskeyUseCase
import com.salazar.cheers.domain.register.RegisterUseCase
import com.salazar.cheers.domain.usecase.SignInUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SignUpUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val username: String = "",
    val sentSignInLinkToEmail: Boolean = false,
    val googleIdToken: String? = null,
    val isSignedIn: Boolean = false,
    val acceptTerms: Boolean = false,
    val page: Int = 0,
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val registerUseCase: RegisterUseCase,
    private val registerPasskeyUseCase: RegisterPasskeyUseCase,
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignUpUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("email")?.let {
            updateWithGoogle(idToken = it)
        }
        stateHandle.get<String>("displayName")?.let {
//            onNameChange(name = it)
        }
    }

    private fun updateWithGoogle(idToken: String) {
        viewModelState.update {
            it.copy(googleIdToken = idToken)
        }
    }

    fun onUsernameChange(username: String) {
        viewModelState.update {
            it.copy(username = username)
        }
    }

    fun onEmailChange(email: String) {
        viewModelState.update {
            it.copy(email = email)
        }
    }

    private fun updateErrorMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateIsSignedIn(isSignedIn: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = isSignedIn)
        }
    }

    fun onAcceptTermsChange(acceptTerms: Boolean) {
        viewModelState.update {
            it.copy(acceptTerms = acceptTerms)
        }
    }

    private fun validateEmail(
        email: String,
    ): Boolean {
        return email.isNotBlank()
    }

    fun nextPage() {
        viewModelState.update {
            it.copy(page = it.page + 1)
        }
    }

    fun onNextClick(activity: Activity) {
        val email = uiState.value.email
        val username = uiState.value.username
        val idToken = uiState.value.googleIdToken

        updateIsLoading(true)

        if (!idToken.isNullOrBlank()) {
            viewModelScope.launch {
                val result = registerUseCase(username)
                when(result) {
                    is Resource.Error -> updateErrorMessage(result.message)
                    is Resource.Loading -> {}//
                    is Resource.Success -> {
                        signInUseCase().onSuccess {
                            updateIsSignedIn(true)
                        }
                    }
                }
                updateIsLoading(false)
            }
        } else {
            viewModelScope.launch {
                registerPasskeyUseCase(activity, username).onSuccess {
                    updateIsSignedIn(true)
                }.onFailure {
                    updateErrorMessage(it.localizedMessage)
                }
                updateIsLoading(false)
            }
        }
    }
}
