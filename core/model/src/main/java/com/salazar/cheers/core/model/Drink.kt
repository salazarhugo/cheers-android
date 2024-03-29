package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "drinks",
)
@Immutable
data class Drink(
    @PrimaryKey
    val id: Int,
    val name: String,
    val icon: String,
    val category: String,
)

val emptyDrink = Drink(
    id = 0,
    name = "Heineiken",
    icon = String(),
    category = String(),
)
