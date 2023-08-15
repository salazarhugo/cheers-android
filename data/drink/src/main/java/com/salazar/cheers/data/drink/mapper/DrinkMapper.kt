package com.salazar.cheers.data.drink.mapper

import cheers.drink.v1.Drink

fun Drink.toDrink(): com.salazar.cheers.core.model.Drink {
    return com.salazar.cheers.core.model.Drink(
        id = id,
        name = name,
        category = category,
        icon = icon,
    )
}