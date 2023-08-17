package com.salazar.cheers.feature.signin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.auth.validators.ValidateEmail
import com.salazar.cheers.core.model.ErrorMessage
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.StoreUserEmail
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
    val isPasswordless: Boolean = true,
    val isSignedIn: Boolean? = null,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val dialog: StateEventWithContent<ErrorMessage> = consumed(),
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storeUserEmail: StoreUserEmail,
    private val authRepository: AuthRepository,
    private val signInUseCases: SignInUseCases,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignInUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("emailLink")?.let { emailLink ->
            signInWithEmailLink(emailLink = emailLink)
        }
        checkIfAlreadySignedIn()
    }

    private fun checkIfAlreadySignedIn() {
        viewModelScope.launch {
            val signedIn = signInUseCases.checkAlreadySignedInUseCase()
            println(signedIn)
            updateIsSignedIn(signedIn)
        }
    }

    private fun signInWithEmailLink(emailLink: String) {
        viewModelScope.launch {
            val result = signInUseCases.signInWithEmailLinkUseCase.invoke(emailLink = emailLink)
            when(result) {
                is Resource.Error -> updateErrorMessage(result.message)
                is Resource.Loading -> updateIsLoading(result.isLoading)
                is Resource.Success -> viewModelState.update { it.copy(isSignedIn = true) }
            }
        }
    }

    fun onPasswordlessChange() {
        viewModelState.update {
            it.copy(isPasswordless = !it.isPasswordless)
        }
    }

    fun onPasswordChange(password: String) {
        viewModelState.update {
            it.copy(password = password)
        }
    }

    fun onEmailChange(email: String) {
        viewModelState.update {
            it.copy(email = email)
        }
    }

    private fun showDialogMessage(errorMessage: ErrorMessage) {
        viewModelState.update {
            it.copy(dialog = triggered(errorMessage))
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

    fun onSignInClick() {
        updateIsLoading(true)
        val state = uiState.value
        val emailValidator = ValidateEmail().invoke(state.email)
        state.apply {
            if (!emailValidator.successful) {
                updateErrorMessage(emailValidator.errorMessage)
                return
            }
            if (isPasswordless)
                sendSignInLinkToEmail(email = email)
            else
                signInWithEmailPassword(email = email, password = password)
        }
    }

    private fun sendSignInLinkToEmail(email: String) {
        authRepository.sendSignInLink(email = email)
            .addOnSuccessListener {
                showDialogMessage(
                    ErrorMessage(
                        title = "Sign In Request Sent",
                        text = "We have sent you an email with instructions on how to sign in. Please check your email and follow the instructions provided.",
                    )
                )
                updateIsLoading(false)
                viewModelScope.launch {
                    storeUserEmail.saveEmail(email)
                }
            }
            .addOnFailureListener {
                updateErrorMessage("Failed to send email")
            }
    }

    fun signInWithOneTap(idToken: String?) {
        if (idToken == null)
            return

        updateIsLoading(true)
        viewModelScope.launch {
            val result = signInUseCases.signInWithOneTapUseCase(idToken = idToken)
            when (result) {
                is Resource.Error -> updateErrorMessage(result.message)
                is Resource.Loading -> updateIsLoading(result.isLoading)
                is Resource.Success -> {
                    val newUser = result.data ?: return@launch
                    if (newUser) {
                        navigateToRegister()
                    } else {
                        updateIsSignedIn(true)
                    }
                }
            }
            updateIsLoading(false)
        }
    }

    private fun signInWithEmailPassword(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            val result = signInUseCases.signInWithEmailAndPasswordUseCase(
                email = email,
                password = password,
            )
            when (result) {
                is Resource.Error -> updateErrorMessage(result.message)
                is Resource.Loading -> updateIsLoading(result.isLoading)
                is Resource.Success -> updateIsSignedIn(true)
            }
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
}
