package com.salazar.cheers.core.ui.components.select_drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.domain.get_coins_balance.GetCoinsBalanceUseCase
import com.salazar.cheers.domain.list_drink.ListDrinkFlowUseCase
import com.salazar.cheers.domain.list_drink.ListDrinkUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrinksUiState(
    val coinsBalance: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val drinks: List<Drink> = emptyList(),
    val searchInput: String = "",
)

@HiltViewModel
class DrinksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCoinsBalanceUseCase: GetCoinsBalanceUseCase,
    private val listDrinkFlowUseCase: ListDrinkFlowUseCase,
    private val listDrinkUseCase: ListDrinkUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DrinksUiState(isLoading = true))
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listDrinkFlowUseCase()
                .collect(::updateDrinks)
        }

        viewModelScope.launch {
            val coins = getCoinsBalanceUseCase()
            viewModelState.update {
                it.copy(coinsBalance = coins)
            }
        }
        viewModelScope.launch {
            listDrinkUseCase(
                query = "",
            ).getOrNull()
        }
    }

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput, isLoading = true)
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            query(query = searchInput)
        }
    }

    private fun query(
        query: String = uiState.value.searchInput.lowercase(),
    ) {
        viewModelScope.launch {
            listDrinkUseCase(
                query = query,
            )
        }
    }

    private fun updateDrinks(drinks: List<Drink>) {
        viewModelState.update {
            it.copy(drinks = drinks)
        }
    }
}
