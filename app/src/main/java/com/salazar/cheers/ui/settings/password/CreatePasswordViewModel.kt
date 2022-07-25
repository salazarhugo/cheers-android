package com.salazar.cheers.ui.settings.password

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CreatePasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val password: String = "",
    val title: String = "",
    val done: Boolean = false,
)

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePasswordUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<Boolean>("hasPassword")?.let { hasPassword ->
            val title = if (hasPassword) "New Password" else "Create Password"
            viewModelState.update {
                it.copy(title = title)
            }
        }
    }

    fun onPasswordChange(password: String) {
        viewModelState.update {
            it.copy(password = password)
        }
    }

    fun onCreatePassword() {
        val password = uiState.value.password
        if (password.isBlank()) {
            updateMessage("Password can't be blank")
            return
        }
        Firebase.auth.currentUser!!.updatePassword(password)
            .addOnSuccessListener {
                Firebase.auth.currentUser?.reload()
                updateDone(true)
            }
            .addOnFailureListener {
                Log.e("AUTH", "Failed updating password $it")
                when (it) {
                    is FirebaseAuthRecentLoginRequiredException -> {}
                    is FirebaseAuthWeakPasswordException -> {}
                    is FirebaseAuthInvalidUserException -> {}
                }
                updateMessage(it.message.toString())
            }
    }

    fun updateDone(done: Boolean) {
        viewModelState.update {
            it.copy(done = done)
        }
    }

    fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

}

