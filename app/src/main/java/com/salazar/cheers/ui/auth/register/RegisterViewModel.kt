package com.salazar.cheers.ui.auth.register

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.util.Utils.validateUsername
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val storeUserEmail: StoreUserEmail,
    private val userRepository: UserRepository,
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

    private fun createUser(
        email: String,
        username: String,
    ) {
        viewModelScope.launch {
            val result = userRepository.createUser(
                username = username,
                email = email,
            )
            if (result.isSuccess)
                viewModelState.update {
                    it.copy(success = true)
                }
            else
                updateErrorMessage(result.exceptionOrNull()?.localizedMessage)
        }
    }

    fun registerUser() {
        val auth = Firebase.auth
        val username = uiState.value.username

        if (auth.currentUser != null) {
            viewModelScope.launch {
                storeUserEmail.getEmail.collect { email ->
                    if (email == null) return@collect
                    createUser(email = email, username = username)
                }
            }
        }

        if (!auth.isSignInWithEmailLink(emailLink)) {
            updateErrorMessage("Invalid link")
            return
        }

        updateIsLoading(true)

        viewModelScope.launch {
            val email = storeUserEmail.getEmail.firstOrNull() ?: return@launch

            auth.signInWithEmailLink(email, emailLink).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    updateErrorMessage("Couldn't sign in with email link")
                    updateIsLoading(false)
                    return@addOnCompleteListener
                }

                createUser(
                    email = email,
                    username = username,
                )
                updateIsLoading(false)
            }
        }
    }
}

