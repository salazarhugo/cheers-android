package com.salazar.cheers.feature.settings.security

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Credential
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.RegisterPasskeyAndSignInUseCase
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.list_passkeys.ListPasskeysUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecurityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val signInMethods: List<String> = emptyList(),
    val passcodeEnabled: Boolean = false,
    val passkeys: List<Credential> = emptyList(),
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val listPasskeysUseCase: ListPasskeysUseCase,
    private val registerPasskeyAndSignInUseCase: RegisterPasskeyAndSignInUseCase,
    private val getAccountUseCase: GetAccountUseCase,
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
            dataStoreRepository.getPasscodeEnabled()
                .collect(::updatePasscodeEnabled)
        }
        viewModelScope.launch {
            val passkeys = listPasskeysUseCase().getOrNull() ?: return@launch
            updatePasskeys(passkeys)
        }
    }

    private fun updatePasscodeEnabled(passcodeEnabled: Boolean) {
        viewModelState.update {
            it.copy(passcodeEnabled = passcodeEnabled)
        }
    }

    private fun updatePasskeys(passkeys: List<Credential>) {
        viewModelState.update {
            it.copy(passkeys = passkeys)
        }
    }

    fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun onCreatePasskeyClick(
        activity: Activity,
    ) {
        viewModelScope.launch {
            val username = dataStoreRepository.getUsername().firstOrNull()
                ?: return@launch

            registerPasskeyAndSignInUseCase(activity, username).fold(
                onSuccess = {
                },
                onFailure = {

                },
            )
        }
    }
}

