package com.salazar.cheers.map.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Settings
import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import com.salazar.cheers.map.data.repository.MapRepository
import com.salazar.cheers.map.domain.usecase.update_ghost_mode.UpdateGhostModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MapSettingsUiState(
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val user: User? = null,
    val settings: Settings? = null,
)

@HiltViewModel
class MapSettingsViewModel @Inject constructor(
    val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val updateGhostModeUseCase: UpdateGhostModeUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapSettingsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect(::updateUser)
        }
        viewModelScope.launch {
            dataStoreRepository.userPreferencesFlow.collect(::updateSettings)
        }
    }

    private fun updateSettings(settings: Settings) {
        viewModelState.update {
            it.copy(settings = settings)
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user)
        }
    }


    fun onGhostModeChange(enabled: Boolean) {
        viewModelScope.launch {
            updateGhostModeUseCase(ghostMode = enabled)
        }
    }
}

