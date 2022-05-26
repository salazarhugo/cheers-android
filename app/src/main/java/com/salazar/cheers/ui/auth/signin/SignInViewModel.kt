package com.salazar.cheers.ui.auth.signin

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.repository.AuthRepository
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
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

@HiltViewModel
class SignInViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storeUserEmail: StoreUserEmail,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
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
    }

    private fun signInWithEmailLink(emailLink: String) {
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
                        viewModelState.update { it.copy(isSignedIn = true) }
                    }
                }
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

    fun onSignInClick() {
        updateIsLoading(true)
        val state = uiState.value
        state.apply {
            if (!email.isEmailValid()) {
                updateErrorMessage("Invalid email")
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
                updateErrorMessage("Email sent")
                updateIsLoading(false)
            }
            .addOnFailureListener {
                updateErrorMessage(it.message)
                updateIsLoading(false)
            }
    }

    private fun signInWithEmailPassword(
        email: String,
        password: String,
    ) {
        if (!validateInput(password)) {
            updateErrorMessage("Password can't be empty")
            updateIsLoading(false)
            return
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        signInWithCredential(credential)
    }

    fun onGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java) ?: return
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            signInWithCredential(credential = credential)
        } catch (e: ApiException) {
            Log.e("Error getting GoogleSignInAccount credential", e.toString())
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnFailureListener {
                updateErrorMessage("Authentication failed: ${it.message}")
                updateIsLoading(false)
            }
            .addOnSuccessListener {
                if (it == null) return@addOnSuccessListener

                getUser(userId = it.user?.uid!!)
            }
    }


    private fun navigateToRegister() {
        viewModelState.update {
            it.copy(navigateToRegister = true)
        }
    }

    private fun updateIsSignedIn(b: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = b)
        }
    }

    private fun getUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserSignIn(userId = userId).collect { result ->
                when(result) {
                    is Resource.Success -> updateIsSignedIn(true)
                    is Resource.Error -> {
                        updateErrorMessage(result.message)
                        navigateToRegister()
                    }
                    is Resource.Loading -> updateIsLoading(result.isLoading)
                }
            }
        }
    }
}

data class SignInUiState(
    val navigateToRegister: Boolean = false,
    val isPasswordless: Boolean = true,
    val isSignedIn: Boolean = false,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val acct: GoogleSignInAccount? = null,
    val password: String = "",
)
