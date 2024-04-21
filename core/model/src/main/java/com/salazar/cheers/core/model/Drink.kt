package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Drink(
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

val coronaExtraDrink = Drink(
    id = String(),
    name = "Corona Extra",
    icon = "https://storage.googleapis.com/cheers-drinks/desperados_original.png",
    category = String(),
)
