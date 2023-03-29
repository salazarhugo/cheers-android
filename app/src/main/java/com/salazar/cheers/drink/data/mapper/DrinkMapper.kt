package com.salazar.cheers.drink.data.mapper

import cheers.drink.v1.Drink

fun Drink.toDrink(): com.salazar.cheers.drink.domain.models.Drink {
    return com.salazar.cheers.drink.domain.models.Drink(
        id = id,
        name = name,
        category = category,
        icon = icon,
    )
}