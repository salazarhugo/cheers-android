package com.salazar.cheers.auth.ui.signin

import android.util.Log
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.core.data.datastore.StoreUserEmail
import com.salazar.cheers.auth.data.AuthRepository
import com.salazar.cheers.auth.domain.usecase.SignInUseCase
import com.salazar.cheers.core.domain.model.ErrorMessage
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.core.data.util.Utils.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val navigateToRegister: Boolean = false,
    val isPasswordless: Boolean = true,
    val isSignedIn: Boolean? = null,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val email: String = "",
    val acct: GoogleSignInAccount? = null,
    val password: String = "",
    val dialog: StateEventWithContent<ErrorMessage> = consumed(),
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storeUserEmail: StoreUserEmail,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val signInUseCase: SignInUseCase,
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
        val isSignedIn = Firebase.auth.currentUser != null

        if (isSignedIn) {
            viewModelScope.launch {
                getUser(userId = Firebase.auth.currentUser?.uid!!)
            }
        }
        else {
            updateIsSignedIn(false)
        }
    }

    private fun signInWithEmailLink(emailLink: String) {
        viewModelScope.launch {
            val result = signInUseCase.invoke(emailLink = emailLink)
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

    private fun validateInput(
        email: String,
    ): Boolean {
        return email.isNotBlank()
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
                showDialogMessage(
                    ErrorMessage(
                    title = "Sign In Request Sent",
                    text = "We have sent you an email with instructions on how to sign in. Please check your email and follow the instructions provided.",
                )
                )
                updateIsLoading(false)
                viewModelScope.launch { storeUserEmail.saveEmail(email) }
            }
            .addOnFailureListener {
                updateErrorMessage("Failed to send email")
            }
    }

    fun signInWithOneTap(idToken: String?) {
        val credential = authRepository.getFirebaseCredentialFromIdToken(idToken = idToken)
        signInWithCredential(credential = credential)
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
        if (task == null) {
            updateIsLoading(false)
            return
        }
        try {
            val account = task.getResult(ApiException::class.java) ?: return
            val credential = authRepository.getFirebaseCredentialFromIdToken(account.idToken)
            signInWithCredential(credential = credential)
        } catch (e: ApiException) {
            Log.e("Error getting GoogleSignInAccount credential", e.toString())
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        authRepository.signInWithCredential(credential)
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

    private fun updateIsSignedIn(isSignedIn: Boolean) {
        viewModelState.update {
            it.copy(isSignedIn = isSignedIn)
        }
    }

    private fun getUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserSignIn(userId = userId).collect { result ->
                when (result) {
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