package com.salazar.cheers.ui.main.map.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class MapSettingsUiState(
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
)

@HiltViewModel
class MapSettingsViewModel @Inject constructor(
    val mapRepository: MapRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapSettingsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {}
}

