package com.salazar.cheers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhoneAuthViewModelState constructor(
    val username: String = "",
    val phoneNumber: String = "",
    val verificationId: String = "",
    val resendingToken: PhoneAuthProvider.ForceResendingToken = PhoneAuthProvider.ForceResendingToken.zza(),
    val verificationCode: String = "",
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val pagerState: PagerState = PagerState(),
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

    fun isLoadingChange(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        viewModelState.update {
            it.copy(phoneNumber = phoneNumber)
        }
    }

    fun onUsernameChange(username: String) {
        viewModelState.update {
            it.copy(username = username)
        }
    }

    fun onCodeSent(
        verificationId: String,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ) {
        viewModelState.update {
            it.copy(
                isLoading = true,
                verificationId = verificationId,
                resendingToken = resendingToken
            )
        }

        viewModelScope.launch {
            uiState.value.pagerState.scrollToPage(1)
        }
    }

    fun onVerificationCodeChange(verificationCode: String) {
        viewModelState.update {
            it.copy(verificationCode = verificationCode)
        }
    }

}