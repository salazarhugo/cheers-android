package com.salazar.cheers.ui.settings.security

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class SecurityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val firebaseUser: FirebaseUser? = null,
    val signInMethods: List<String> = emptyList(),
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SecurityUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            authRepository.getUserIdToken().collect { user ->
                viewModelState.update {
                    it.copy(firebaseUser = user)
                }
                if (user == null) return@collect

                val email = user.email

                if (email != null && email.isNotBlank())
                    getSignInMethods(email)
            }
        }
    }

    fun onResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java) ?: return
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            linkWithCredential(credential = credential)
        } catch (e: ApiException) {
            Log.e("SIGN IN", e.toString())
        }
    }

    private fun linkWithCredential(credential: AuthCredential ) {
        val user = uiState.value.firebaseUser ?: return

        user.linkWithCredential(credential).addOnSuccessListener {
            user.reload()
        }.addOnFailureListener {
            updateMessage(it.message.toString())
        }
    }

    fun onUnlink(provider: String) {
        val user = uiState.value.firebaseUser ?: return

        user.unlink(provider).addOnSuccessListener {
            FirebaseAuth.getInstance().currentUser?.reload()
        }.addOnFailureListener {
            updateMessage(it.toString())
        }
    }

    fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun getSignInMethods(email: String) {
        Firebase.auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val signInMethods = result.signInMethods!!
                viewModelState.update {
                    it.copy(signInMethods = signInMethods)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Sign In", "Error getting sign in methods for user", exception)
            }
    }
}

