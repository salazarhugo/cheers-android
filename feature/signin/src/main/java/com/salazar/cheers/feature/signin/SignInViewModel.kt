package com.salazar.cheers.feature.signin

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ErrorMessage
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.cheers.domain.usecase.SignInWithCredentialManagerFlowUseCase
import com.salazar.common.util.Resource
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
    val navigateToRegister: Boolean = false,
    val isSignedIn: Boolean? = null,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val username: String = "",
    val dialog: StateEventWithContent<ErrorMessage> = consumed(),
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val signInUseCases: SignInUseCases,
    private val signInWithCredentialManagerFlowUseCase: SignInWithCredentialManagerFlowUseCase,
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

    fun onGoogleButtonClick() {
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
                context,
                username = username
            )
            updateIsLoading(false)
        }
    }

    private fun navigateToRegister() {
        viewModelState.update {
            it.copy(navigateToRegister = true)
        }
    }

    private fun updateIsSignedIn(isSignedIn: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = isSignedIn)
        }
    }

    fun showSigningOptions(
        context: Context,
        username: String? = null,
    ) {
        viewModelScope.launch {
            signInWithCredentialManagerFlowUseCase(
                context,
                username = username,
            ).collect {
                when(it) {
                    is Resource.Success -> {
                        updateIsSignedIn(true)
                    }
                    is Resource.Error -> {
                        if (it.data is AuthRepository.NotRegisteredException) {
                            navigateToRegister()
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
