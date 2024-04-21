package com.salazar.cheers.feature.profile.cheerscode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class CheerscodeUiState(
    val isLoading: Boolean,
    val errorMessages: String,
)


data class CheerscodeViewModelState(
    val isLoading: Boolean = false,
    val errorMessages: String = "",
)

@HiltViewModel
class CheerscodeViewModel @Inject constructor(
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CheerscodeViewModelState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value,
        )

    init {
    }
}