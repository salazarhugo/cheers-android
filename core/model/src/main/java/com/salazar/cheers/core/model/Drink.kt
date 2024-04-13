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
    val id: String,
    val name: String,
    val icon: String,
    val category: String,
)

val emptyDrink = Drink(
    id = String(),
    name = "Heineiken",
    icon = String(),
    category = String(),
)
