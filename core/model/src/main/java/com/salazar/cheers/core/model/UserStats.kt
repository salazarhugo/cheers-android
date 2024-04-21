package com.salazar.cheers.core.model

data class UserStats(
    val id: String = "",
    val username: String = "",
    val drinks: Int = 0,
    val maxDrunkenness: Float = 0f,
    val avgDrunkenness: Float = 0f,
    val favoriteDrink: String = "",
)