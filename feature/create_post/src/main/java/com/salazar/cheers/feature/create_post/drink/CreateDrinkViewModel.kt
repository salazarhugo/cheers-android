package com.salazar.cheers.feature.create_post.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.domain.create_drink.CreateDrinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDrinkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val createDrinkUseCase: CreateDrinkUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreateDrinkUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        val icons = listOf(
            "https://storage.googleapis.com/cheers-drinks/beer.svg",
            "https://storage.googleapis.com/cheers-drinks/Vodka.svg",
            "https://storage.googleapis.com/cheers-drinks/Cocktail.svg",
            "https://storage.googleapis.com/cheers-drinks/RedWine.svg",
            "https://storage.googleapis.com/cheers-drinks/RoseWine.svg",
            "https://storage.googleapis.com/cheers-drinks/WhiteWine.svg",
            "https://storage.googleapis.com/cheers-drinks/Shot.svg",
            "https://storage.googleapis.com/cheers-drinks/Whiskey.svg",
            "https://storage.googleapis.com/cheers-drinks/champagne.svg",
            "https://storage.googleapis.com/cheers-drinks/cider.svg",
        )
        viewModelState.update {
            it.copy(icons = icons)
        }
    }

    fun onCreateDrink(icon: String?) {
        val viewModelState = viewModelState.value
        viewModelScope.launch {
            createDrinkUseCase(
                name = viewModelState.name,
                icon = icon,
            )
        }
    }

    fun onNameChange(name: String) {
        viewModelState.update {
            it.copy(name = name)
        }
    }

    fun onIconChange(icon: String) {
        viewModelState.update {
            it.copy(icon = icon)
        }
    }

}
