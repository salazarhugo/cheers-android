package com.salazar.cheers.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "drinks",
)
data class Drink(
    @PrimaryKey
    val id: Int,
    val name: String,
    val icon: String,
    val category: String,
)