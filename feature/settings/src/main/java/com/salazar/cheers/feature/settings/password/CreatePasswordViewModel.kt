package com.salazar.cheers.feature.settings.password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val authRepository: com.salazar.cheers.data.auth.AuthRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePasswordUiState())

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

    fun onCreatePassword(onComplete: () -> Unit) {
        val password = uiState.value.password
        if (password.isBlank()) {
            updateMessage("Password can't be blank")
            return
        }
        viewModelScope.launch {
            authRepository.updatePassword(password = password).collect {
                when(it) {
                    is Resource.Success -> { onComplete() }
                    is Resource.Error -> updateMessage(it.message.orEmpty())
                    is Resource.Loading -> updateIsLoading(it.isLoading)
                }
            }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
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

