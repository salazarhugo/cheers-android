package com.salazar.cheers.ui.auth.signin

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignInUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {}

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

    private fun updateIsSignedIn(isSignedIn: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = isSignedIn)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
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

    fun signInWithEmailPassword() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (!validateInput(email, password)) {
            updateErrorMessage("Fields can't be empty")
            return
        }

        updateIsLoading(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                updateErrorMessage("Authentication failed: ${it.message}")
            }
            .addOnSuccessListener {
                getAndSaveRegistrationToken()
            }
            .addOnCompleteListener { task ->
                updateIsLoading(false)
            }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                task.addOnFailureListener {
                    Log.e("GOOGLE", it.toString())
                }
                if (task.isSuccessful) {
                    signInSuccessful(acct)
                    Log.e("GOOGLE", "SUCCESSFUL")
                } else {
                }
            }
    }

    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        if (acct == null) return
        FirestoreUtil.checkIfUserExists { exists ->
            if (exists) {
                updateIsSignedIn(true)
                getAndSaveRegistrationToken()
            } else {
                viewModelState.update {
                    it.copy(firstTime = true, email = acct.email!!)
                }
            }
        }
    }
}

data class SignInUiState(
    val isSignedIn: Boolean = false,
    val firstTime: Boolean = false,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
)
