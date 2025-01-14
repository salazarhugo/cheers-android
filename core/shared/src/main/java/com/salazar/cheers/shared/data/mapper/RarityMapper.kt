package com.salazar.cheers.shared.data.mapper

import cheers.type.DrinkOuterClass
import com.salazar.cheers.core.model.Rarity

fun Rarity.toRarityPb(): DrinkOuterClass.Drink.Rarity {
    return when (this) {
        Rarity.DEFAULT -> DrinkOuterClass.Drink.Rarity.DEFAULT
        Rarity.COMMON -> DrinkOuterClass.Drink.Rarity.COMMON
        Rarity.UNCOMMON -> DrinkOuterClass.Drink.Rarity.UNCOMMON
        Rarity.RARE -> DrinkOuterClass.Drink.Rarity.RARE
        Rarity.MYTHICAL -> DrinkOuterClass.Drink.Rarity.MYTHICAL
        Rarity.LEGENDARY -> DrinkOuterClass.Drink.Rarity.LEGENDARY
        Rarity.ANCIENT -> DrinkOuterClass.Drink.Rarity.ANCIENT
        Rarity.EXCEEDINGLY_RARE -> DrinkOuterClass.Drink.Rarity.EXCEEDINGLY_RARE
        Rarity.IMMORTAL -> DrinkOuterClass.Drink.Rarity.IMMORTAL
    }
}

fun DrinkOuterClass.Drink.Rarity.toRarity(): Rarity {
    return when (this) {
        DrinkOuterClass.Drink.Rarity.DEFAULT -> Rarity.DEFAULT
        DrinkOuterClass.Drink.Rarity.COMMON -> Rarity.COMMON
        DrinkOuterClass.Drink.Rarity.UNCOMMON -> Rarity.UNCOMMON
        DrinkOuterClass.Drink.Rarity.RARE -> Rarity.RARE
        DrinkOuterClass.Drink.Rarity.MYTHICAL -> Rarity.MYTHICAL
        DrinkOuterClass.Drink.Rarity.LEGENDARY -> Rarity.LEGENDARY
        DrinkOuterClass.Drink.Rarity.ANCIENT -> Rarity.ANCIENT
        DrinkOuterClass.Drink.Rarity.EXCEEDINGLY_RARE -> Rarity.EXCEEDINGLY_RARE
        DrinkOuterClass.Drink.Rarity.IMMORTAL -> Rarity.IMMORTAL
        DrinkOuterClass.Drink.Rarity.UNRECOGNIZED -> Rarity.DEFAULT
    }
}
