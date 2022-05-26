package com.salazar.cheers.ui.auth.register

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val username: String = "",
    val termsAccepted: Boolean = false,
    val success: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storeUserEmail: StoreUserEmail,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RegisterUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    lateinit var emailLink: String
    lateinit var username: String

    init {
        stateHandle.get<String>("emailLink")?.let {
            emailLink = it
        }
        stateHandle.get<String>("username")?.let { username ->
            this.username = username
            viewModelState.update { it.copy(username = username) }
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

    fun register() {
        val auth = Firebase.auth

        if (!auth.isSignInWithEmailLink(emailLink))
            return
        updateIsLoading(true)

        viewModelScope.launch {
            storeUserEmail.getEmail.collect { email ->

                if (email == null) return@collect

                auth.signInWithEmailLink(email, emailLink).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("YES", "Error signing in with email link", task.exception)
                        return@addOnCompleteListener
                    }

                    Log.d("YES", "Successfully signed in with email link!")

                    viewModelScope.launch {
                        val user = userRepository.createUser(
                            username = username,
                            email = email,
                        )
                        if (user != null)
                            viewModelState.update { it.copy(success = true) }
                    }
                }
            }
        }
    }

}

