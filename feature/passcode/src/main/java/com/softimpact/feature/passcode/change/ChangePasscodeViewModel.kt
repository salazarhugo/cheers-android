package com.softimpact.feature.passcode.change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChangePasscodeUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val success: Boolean = false,
    val input: String = "",
    val newPasscode: String = "",
    val isConfirmationScreen: Boolean = false,
)

@HiltViewModel
class ChangePasscodeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChangePasscodeUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            dataStoreRepository.getBiometricEnabled().collect { biometricEnabled ->
//                viewModelState.update {
//                    it.copy(biometricEnabled = biometricEnabled)
//                }
            }
        }
    }

    fun onSubmit(onComplete: () -> Unit) {
        val state = viewModelState.value

        if (state.isConfirmationScreen) {

            if (state.input != state.newPasscode) {
                updateError("Passcodes don't match. Please try again.")
                clearInputPin()
                return
            }

            viewModelScope.launch {
                dataStoreRepository.updatePasscode(state.input)
                onComplete()
            }
        } else {
            updateNewPasscode(state.input)
            clearInputPin()
            updateIsConfirmationScreen(true)
        }
    }


    private fun updateIsConfirmationScreen(isConfirmationScreen: Boolean) {
        viewModelState.update {
            it.copy(isConfirmationScreen = isConfirmationScreen)
        }
    }

    fun updateError(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateNewPasscode(newPasscode: String) {
        viewModelState.update {
            it.copy(newPasscode = newPasscode)
        }
    }

    fun updateSuccess(success: Boolean) {
        viewModelState.update {
            it.copy(success = success)
        }
    }

    private fun clearInputPin() {
        viewModelState.update {
            it.copy(input = "")
        }
    }

    fun onRemoveLastDigit() {
        viewModelState.update {
            it.copy(input = it.input.dropLast(1))
        }
    }

    fun onAddDigit(digit: Int) {
        viewModelState.update {
            it.copy(input = it.input + digit)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }
}