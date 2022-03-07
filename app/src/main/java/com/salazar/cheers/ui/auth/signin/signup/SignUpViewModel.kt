package com.salazar.cheers.ui.auth.signin.signup

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Utils.isEmailValid
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
    val username: String = "",
    val isUsernameAvailable: Boolean = false,
    val email: String = "",
    val password: String = "",
    val isSignedIn: Boolean = false,
    val page: Int = 0,
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignUpUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {}

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

    private fun validateInput(
        email: String,
        password: String
    ): Boolean {
        if (email.isBlank() || password.isBlank())
            return false
        return true
    }

    private fun isLowerCase(username: String): Boolean {
        val isLower = username == username.lowercase()
        if (!isLower)
            updateErrorMessage("Must be lowercase")
        return isLower
    }

    private fun hasValidChars(username: String): Boolean {
        val regex = Regex("^[._a-z0-9]+\$")
        val validChars = username.matches(regex)
        if (!validChars)
            updateErrorMessage("Only dots and underscores are allowed")
        return validChars
    }

    private fun validateUsername(username: String): Boolean {
        val regex = Regex("^(?!.*\\.\\.)(?!.*\\.\$)[^\\W][\\w.]{0,29}\$")
        return isLowerCase(username) && hasValidChars(username) && username.matches(regex)
    }

    fun prevPage() {
        viewModelState.update {
            if (it.page < 1) return
            it.copy(page = it.page - 1)
        }
    }

    fun nextPage() {
        viewModelState.update {
            it.copy(page = it.page + 1)
        }
    }

    fun verifyEmail() {
        if (uiState.value.email.isEmailValid())
            nextPage()
    }

    fun verifyPassword() {
        val state = uiState.value
        if (state.password.isNotBlank())
            nextPage()
    }

    fun checkUsername() {
        val username = uiState.value.username
        updateIsLoading(true)

        if (!validateUsername(username = username)) {
            updateErrorMessage("Invalid username")
            updateIsLoading(false)
            return
        }

        isUsernameAvailable(username) { result ->

            if (result is Result.Success && !result.data) {
                updateErrorMessage("This username is taken")
                updateIsLoading(false)
                return@isUsernameAvailable
            }

            viewModelState.update {
                when (result) {
                    is Result.Success -> {
                        it.copy(
                            isUsernameAvailable = result.data,
                            username = username,
                            isLoading = false,
                            page = it.page + 1,
                        )
                    }
                    is Result.Error -> {
                        it.copy(
                            isUsernameAvailable = false,
                            errorMessage = it.errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun isUsernameAvailable(
        username: String,
        onResponse: (Result<Boolean>) -> Unit
    ) {
        viewModelScope.launch {
            val result = Neo4jUtil.isUsernameAvailable(username)
            onResponse(result)
        }
    }

    private fun getAndSaveRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            viewModelScope.launch {
                MyFirebaseMessagingService.addTokenToNeo4j(token)
            }
        }
    }

    private fun signInSuccessful(
        email: String,
        username: String
    ) {
        FirestoreUtil.checkIfUserExists { exists ->
            if (exists) {
                updateIsSignedIn(true)
                getAndSaveRegistrationToken()
            } else {
                FirestoreUtil.initCurrentUserIfFirstTime(email = email, username = username) {
                    updateIsSignedIn(true)
                    getAndSaveRegistrationToken()
                }
            }
        }
    }

    fun createAccount() {
        val state = uiState.value
        val username = state.username
        val email = state.email
        val password = state.password

        updateIsLoading(true)

        if (!validateInput(email, password)) {
            updateErrorMessage("Fields can't be empty")
            return
        }

        isUsernameAvailable(username) { result ->
            when (result) {
                is Result.Success -> {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signInSuccessful(email = email, username = username)
                            } else {
                                // If sign in fails, display a message to the user.
                                updateErrorMessage(task.exception?.message)
                            }
                            updateIsLoading(false)
                        }
                }
                else -> {
                    updateErrorMessage("Username not available")
                    return@isUsernameAvailable
                }
            }
        }
    }
}
