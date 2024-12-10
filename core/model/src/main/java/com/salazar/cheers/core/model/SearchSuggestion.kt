package com.salazar.cheers.core.model

data class SearchSuggestion(
    val name: String,
    val icon: String? = null,
    val address: String? = null,
    val city: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
