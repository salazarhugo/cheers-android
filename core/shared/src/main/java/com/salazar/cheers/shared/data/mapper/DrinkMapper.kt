package com.salazar.cheers.shared.data.mapper

import cheers.type.DrinkOuterClass


fun DrinkOuterClass.Drink.toDrink(): com.salazar.cheers.core.model.Drink {
    return com.salazar.cheers.core.model.Drink(
        id = id,
        name = name,
        userID = userId,
        brand = brand,
        price = price.toInt(),
        description = description,
        icon = icon,
        color = color,
        privacy = privacy.toPrivacy(),
        rarity = rarity.toRarity(),
    )
}