package com.salazar.cheers.ui.main.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.entities.UserStats
import com.salazar.cheers.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrinkingStatsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val userStats: UserStats? = null,
)

@HiltViewModel
class DrinkingStatsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DrinkingStatsUiState(isLoading = true))
    private lateinit var username: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("username")?.let {
            username = it
        }

        viewModelScope.launch {
            val userStats = userRepository.getUserStats(username = username)
            try {
                viewModelState.update { it.copy(userStats = userStats) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
