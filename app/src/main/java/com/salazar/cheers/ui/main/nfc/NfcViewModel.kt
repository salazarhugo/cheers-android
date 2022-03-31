package com.salazar.cheers.ui.main.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NfcUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

@HiltViewModel
class NfcViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(NfcUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }
}

