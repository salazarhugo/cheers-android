package com.salazar.cheers

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PhoneAuthViewModelState constructor(
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
)

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PhoneAuthViewModelState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    fun onPhoneNumberChange(phoneNumber: String) {
        viewModelState.update {
            it.copy(phoneNumber = phoneNumber)
        }
    }

    fun onVerificationCodeChange(verificationCode: String) {
        viewModelState.update {
            it.copy(verificationCode = verificationCode)
        }
    }

}