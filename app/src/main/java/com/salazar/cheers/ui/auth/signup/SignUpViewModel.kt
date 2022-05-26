package com.salazar.cheers.ui.auth.signup

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.util.Utils.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.prefs.Preferences
import javax.inject.Inject


data class SignUpUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val username: String = "",
    val isUsernameAvailable: Boolean = false,
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val sentSignInLinkToEmail: Boolean = false,
    val withGoogle: Boolean = false,
    val isSignedIn: Boolean = false,
    val acceptTerms: Boolean = false,
    val page: Int = 0,
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val userDao: UserDao,
    private val userRepository: UserRepository,
    private val storeUserEmail: StoreUserEmail,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SignUpUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun onClearUsername() {
        viewModelState.update {
            it.copy(username = "")
        }
    }

    fun updateWithGoogle(withGoogle: Boolean) {
        viewModelState.update {
            it.copy(withGoogle = withGoogle)
        }
    }

    fun onNameChange(name: String) {
        viewModelState.update {
            it.copy(name = name)
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

    fun onAcceptTermsChange(acceptTerms: Boolean) {
        viewModelState.update {
            it.copy(acceptTerms = acceptTerms)
        }
    }


    private fun validateEmail(
        email: String,
    ): Boolean {
        return email.isNotBlank()
    }

    private fun validatePassword(
        password: String
    ): Boolean {
        return password.isNotBlank()
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
            sendSignInLinkToEmail()
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

            if (!result) {
                updateErrorMessage("This username is taken")
                updateIsLoading(false)
                return@isUsernameAvailable
            }

            viewModelState.update {
                val page = if (it.email.isNotBlank()) it.page + 3 else it.page + 1
                it.copy(
                    isUsernameAvailable = result,
                    username = username,
                    isLoading = false,
                    page = page,
                )
            }
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

    private suspend fun getAndSaveRegistrationToken() {
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

    private fun sendSignInLinkToEmail() {
        val state = uiState.value
        val username = state.username
        val email = state.email

        updateIsLoading(true)

        viewModelScope.launch {
            val actionCodeSettings = actionCodeSettings {
                url = "https://cheers-a275e.web.app/register/$username"
                handleCodeInApp = true
                setIOSBundleId("com.salazar.cheers")
                setAndroidPackageName(
                    "com.salazar.cheers",
                    true,
                    "8"
                )
            }

            Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("Email Link", it.exception.toString())
                    return@addOnCompleteListener
                }

                viewModelScope.launch { storeUserEmail.saveEmail(email) }
                updateIsLoading(false)
                nextPage()
            }
        }
    }
}
