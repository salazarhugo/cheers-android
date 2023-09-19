package com.softimpact.feature.passcode.create

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


data class CreatePasscodeUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val success: Boolean = false,
    val inputPin: String = "",
)

@HiltViewModel
class CreatePasscodeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePasscodeUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
//            dataStoreRepository.userPreferencesFlow.collect { appSettings ->
//                viewModelState.update {
//                    it.copy(settings = appSettings)
//                }
//            }
        }
    }

    fun onSubmit(onSuccess: () -> Unit) {
        val state = viewModelState.value
        val passcode = state.inputPin

        viewModelScope.launch {
            dataStoreRepository.updatePasscode(passcode)
            onSuccess()
        }
    }


    fun updateError(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun updateSuccess(success: Boolean) {
        viewModelState.update {
            it.copy(success = success)
        }
    }

    fun clearInputPin() {
        viewModelState.update {
            it.copy(inputPin = "")
        }
    }

    fun onRemoveLastDigit() {
        viewModelState.update {
            it.copy(inputPin = it.inputPin.dropLast(1))
        }
    }

    fun onAddDigit(digit: Int) {
        viewModelState.update {
            it.copy(inputPin = it.inputPin + digit)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }
}