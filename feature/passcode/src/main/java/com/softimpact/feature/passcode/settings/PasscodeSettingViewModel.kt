package com.softimpact.feature.passcode.settings

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


data class PasscodeLockSettingUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val success: Boolean = false,
    val biometricEnabled: Boolean = false,
    val hideContent: Boolean = false,
)

@HiltViewModel
class PasscodeSettingViewModel @Inject constructor(
    private val dataStoreRepositoryImpl: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PasscodeLockSettingUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            dataStoreRepositoryImpl.userPreferencesFlow.collect { settings ->
                viewModelState.update {
                    it.copy(biometricEnabled = settings.hasBiometric, hideContent = settings.hideContent)
                }
            }
        }
    }

    fun onSubmit() {
    }

    fun onTurnOffPasscode(onComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreRepositoryImpl.updatePasscode("")
            dataStoreRepositoryImpl.updateHideContent(false)
            onComplete()
        }
    }

    fun toggleHideContent() {
        viewModelScope.launch {
            dataStoreRepositoryImpl.toggleHideContent()
        }
    }

    fun toggleBiometric() {
        viewModelScope.launch {
            dataStoreRepositoryImpl.toggleBiometric()
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

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }
}