package com.salazar.cheers.feature.create_post.drink


data class CreateDrinkUiState(
    val name: String = "",
    val icon: String = "",
    val icons: List<String> = emptyList(),
    val isLoading: Boolean,
    val errorMessage: String? = null,
)