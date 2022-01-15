package com.salazar.cheers.internal

import androidx.room.Entity

data class Beverage(
    val name: String,
    val alcohol: Float,
    val icon: String,
)