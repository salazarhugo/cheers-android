package com.salazar.cheers.ui.auth.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.repository.AuthRepository
import com.salazar.cheers.data.repository.ChatRepository
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
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
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

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun validateInput(
        email: String,
    ): Boolean {
        return email.isNotBlank()
    }

    private fun getAndSaveRegistrationToken() {
        if (FirebaseAuth.getInstance().currentUser == null)
            return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            viewModelScope.launch {
                chatRepository.addToken(token = token)
                MyFirebaseMessagingService.addTokenToNeo4j(token)
                FirestoreUtil.addFCMRegistrationToken(token = token)
            }
        }
    }

    fun signInWithEmailPassword() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (!validateInput(email )) {
            updateErrorMessage("Email can't be empty")
            return
        }

        updateIsLoading(true)

        if (password.isBlank())
            authRepository.sendSignInLink(email)
        else
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
            .addOnFailureListener {
                updateErrorMessage("Authentication failed: ${it.message}")
            }
            .addOnSuccessListener {
                val isNew = it.additionalUserInfo!!.isNewUser

                getAndSaveRegistrationToken()
                signInSuccessful(acct)
            }
            .addOnCompleteListener { task ->
                updateIsLoading(false)
            }
    }

    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        if (acct == null) return
        viewModelState.update {
            it.copy(acct = acct)
        }
    }
}

data class SignInUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val acct: GoogleSignInAccount? = null,
    val password: String = "",
)
