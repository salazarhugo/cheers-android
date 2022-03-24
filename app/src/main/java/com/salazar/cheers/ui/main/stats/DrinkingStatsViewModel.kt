package com.salazar.cheers.ui.main.stats

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.entities.UserStats
import com.salazar.cheers.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DrinkingStatsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val userStats: UserStats? = null,
)

class DrinkingStatsViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    @Assisted val username: String,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DrinkingStatsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            val userStats = userRepository.getUserStats(username = username)
            try {
                viewModelState.update { it.copy(userStats = userStats) }
            } catch (e: Exception) {
            }
        }
    }

    @AssistedFactory
    interface DrinkingStatsViewModelFactory {
        fun create(username: String): DrinkingStatsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: DrinkingStatsViewModelFactory,
            username: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(username = username) as T
            }
        }
    }
}

@Composable
fun drinkingStatsViewModel(username: String): DrinkingStatsViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).drinkingStatsViewModelFactory()

    return viewModel(factory = DrinkingStatsViewModel.provideFactory(factory, username = username))
}
