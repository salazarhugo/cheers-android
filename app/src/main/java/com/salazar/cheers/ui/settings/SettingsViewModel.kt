package com.salazar.cheers.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.CheersDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private var cheersDao: CheersDao,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
)

