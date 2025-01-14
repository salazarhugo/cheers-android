package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Drink(
    val id: String,
    val userID: UserID = UserID(),
    val name: String,
    val icon: String,
    val price: Int = 0,
    val brand: String = "",
    val description: String = "",
    val color: String = "",
    val privacy: Privacy = Privacy.PRIVATE,
    val lastUsed: Long = 0,
    val rarity: Rarity = Rarity.DEFAULT,
)

val emptyDrink = Drink(
    id = String(),
    name = "Heineiken",
    icon = String(),
)

val createDrink = Drink(
    id = "add",
    name = "Create drink",
    icon = "https://storage.googleapis.com/cheers-drinks/beer.png",
)

val coronaExtraDrink = Drink(
    id = String(),
    name = "Corona Extra",
    icon = "https://storage.googleapis.com/cheers-drinks/desperados_original.png",
    price = 1241,
)
