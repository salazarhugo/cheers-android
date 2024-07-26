package com.salazar.cheers.feature.signin

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ErrorMessage
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.cheers.domain.get_remote_config.CheckFeatureEnabledUseCase
import com.salazar.cheers.domain.models.RemoteConfigParameter
import com.salazar.cheers.domain.usecase.SignInWithCredentialManagerFlowUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val navigateToRegister: String? = null,
    val isSignedIn: Boolean? = null,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val username: String = "",
    val dialog: StateEventWithContent<ErrorMessage> = consumed(),
    val isPasskeyEnabled: Boolean = false,
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val signInUseCases: SignInUseCases,
    private val signInWithCredentialManagerFlowUseCase: SignInWithCredentialManagerFlowUseCase,
    private val checkFeatureEnabledUseCase: CheckFeatureEnabledUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignInUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        checkIfAlreadySignedIn()
        checkPasskeyEnabled()
    }

    private fun checkPasskeyEnabled() {
        viewModelScope.launch {
            val isPasskeyEnabled = checkFeatureEnabledUseCase(
                RemoteConfigParameter.Passkey
            ).getOrDefault(false)
            viewModelState.update {
                it.copy(isPasskeyEnabled = isPasskeyEnabled)
            }
        }
    }

    private fun checkIfAlreadySignedIn() {
        viewModelScope.launch {
            val signedIn = signInUseCases.checkAlreadySignedInUseCase()
            println(signedIn)
            updateIsSignedIn(signedIn)
        }
    }

    fun onUsernameChanged(username: String) {
        viewModelState.update {
            it.copy(username = username)
        }
    }

    private fun updateErrorMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage, isLoading = false)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onGoogleButtonClick(
        context: Context,
    ) {
        showSigningOptions(
            context = context,
            username = "cheers.social",
        )
    }

    fun onSignInClick(
        username: String,
        onSuccess: () -> Unit,
        context: Context,
    ) {
        val username = uiState.value.username
        updateIsLoading(true)
        viewModelScope.launch {
            showSigningOptions(
                context = context,
                username = username
            )
            updateIsLoading(false)
        }
    }

    private fun navigateToRegister(idToken: String) {
        viewModelState.update {
            it.copy(navigateToRegister = idToken)
        }
    }

    private fun updateIsSignedIn(isSignedIn: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = isSignedIn)
        }
    }

    private fun showSigningOptions(
        context: Context,
        username: String? = null,
    ) {
        viewModelScope.launch {
            signInWithCredentialManagerFlowUseCase(
                activityContext = context,
                username = username,
            ).collect {
                when(it) {
                    is Resource.Success -> {
                        Log.d("SignInViewModel", "showSigningOptions: SUCCESS")
                        updateIsSignedIn(true)
                    }
                    is Resource.Error -> {
                        if (it.data is AuthRepository.NotRegisteredException) {
                            navigateToRegister((it.data as AuthRepository.NotRegisteredException).message.orEmpty())
                            return@collect
                        }
                        updateErrorMessage(it.message)
                    }
                    is Resource.Loading -> {
                        updateIsLoading(it.isLoading)
                    }
                }
            }
        }
    }
}
