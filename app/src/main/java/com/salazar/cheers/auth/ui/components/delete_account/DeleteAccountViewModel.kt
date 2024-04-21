package com.salazar.cheers.auth.ui.components.delete_account


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.domain.delete_account.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeleteAccountUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DeleteAccountUiState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    fun deleteAccount(
        onCompleted: (success: Boolean) -> Unit,
    ) {
        updateIsLoading(true)

        viewModelScope.launch {
            deleteAccountUseCase()
                .onSuccess {
                    onCompleted(true)
                }
                .onFailure {
                    updateError(it.message.toString())
                    onCompleted(false)
                }
        }
    }

    private fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }
}
