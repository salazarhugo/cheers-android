package com.salazar.cheers.feature.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.domain.list_drink.GetDrinkUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrinkUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val drink: Drink? = null,
)

@HiltViewModel
class DrinkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDrinkUseCase: GetDrinkUseCase,
) : ViewModel() {
    private val args = savedStateHandle.toRoute<DrinkScreen>()

    private val viewModelState = MutableStateFlow(DrinkUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            val drink = getDrinkUseCase(args.drinkID).getOrNull()
            updateDrink(drink)
        }
    }

    private fun updateDrink(drink: Drink?) {
        viewModelState.update { it.copy(drink = drink) }
    }
}
