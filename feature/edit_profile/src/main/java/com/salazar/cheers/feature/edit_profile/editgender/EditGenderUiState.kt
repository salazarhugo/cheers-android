package com.salazar.cheers.feature.edit_profile.editgender


data class EditGenderUiState(
    val isLoading: Boolean,
    val errorMessages: List<String>,
    val gender: String,
)