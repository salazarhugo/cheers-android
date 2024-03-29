package com.salazar.cheers.feature.map.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Settings
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.data.map.MapRepository
import com.salazar.cheers.feature.map.domain.usecase.update_ghost_mode.UpdateGhostModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val mapRepository: com.salazar.cheers.data.map.MapRepository,
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

