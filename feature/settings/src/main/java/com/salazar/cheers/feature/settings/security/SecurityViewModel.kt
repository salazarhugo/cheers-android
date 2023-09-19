package com.salazar.cheers.feature.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecurityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
//    val firebaseUser: FirebaseUser? = null,
    val signInMethods: List<String> = emptyList(),
    val passcodeEnabled: Boolean = false,
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
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
            dataStoreRepository.getPasscode().collect { passcode ->
                viewModelState.update {
                    it.copy(passcodeEnabled = passcode.isNotBlank())
                }
            }
        }
    }

//    fun onResult(task: Task<GoogleSignInAccount>?) {
//        try {
//            val account = task?.getResult(ApiException::class.java) ?: return
//            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//            linkWithCredential(credential = credential)
//        } catch (e: ApiException) {
//            Log.e("SIGN IN", e.toString())
//        }
//    }

//    private fun linkWithCredential(credential: AuthCredential) {
//        val user = uiState.value.firebaseUser ?: return
//
//        user.linkWithCredential(credential).addOnSuccessListener {
//            user.reload()
//        }.addOnFailureListener {
//            updateMessage(it.message.toString())
//        }
//    }
//
//    fun onUnlink(provider: String) {
//        val user = uiState.value.firebaseUser ?: return
//
//        user.unlink(provider).addOnSuccessListener {
//            FirebaseAuth.getInstance().currentUser?.reload()
//        }.addOnFailureListener {
//            updateMessage(it.toString())
//        }
//    }

    fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun getSignInMethods(email: String) {
//        Firebase.auth.fetchSignInMethodsForEmail(email)
//            .addOnSuccessListener { result ->
//                val signInMethods = result.signInMethods!!
//                viewModelState.update {
//                    it.copy(signInMethods = signInMethods)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Sign In", "Error getting sign in methods for user", exception)
//            }
    }
}

