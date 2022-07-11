package com.salazar.cheers.internal

import androidx.annotation.DrawableRes
import com.salazar.cheers.R

enum class Beverage(
    val displayName: String,
    val alcohol: Float,
    @DrawableRes val icon: Int,
) {
    NONE(
        displayName = "",
        alcohol = 0f,
        icon = R.drawable.ic_cocktail,
    ),
    WATER(
        displayName = "Water",
        alcohol = 0f,
        icon = R.drawable.water,
    ),
    BEER(
        displayName = "Beer",
        alcohol = 5f,
        icon = R.drawable.beer,
    ),
    RED_WINE(
        displayName = "Red Wine",
        alcohol = 5f,
        icon = R.drawable.redwine,
    ),
    WHITE_WINE(
        displayName = "White Wine",
        alcohol = 5f,
        icon = R.drawable.whitewine,
    ),
    ROSE_WINE(
        displayName = "Rose Wine",
        alcohol = 5f,
        icon = R.drawable.rosewine,
    ),
    CHAMPAGNE(
        displayName = "Champagne",
        alcohol = 5f,
        icon = R.drawable.champagne,
    ),
    PROSECCO(
        displayName = "Prosecco",
        alcohol = 5f,
        icon = R.drawable.ic_cocktail,
    ),
    SHOT(
        displayName = "Shot",
        alcohol = 5f,
        icon = R.drawable.shot__2_
    ),
    MARTINI(
        displayName = "Martini",
        alcohol = 5f,
        icon = R.drawable.martini,
    ),
    COCKTAIL(
        displayName = "Cocktail",
        alcohol = 5f,
        icon = R.drawable.ic_cocktail,
    ),
    WHISKEY(
        displayName = "Whiskey",
        alcohol = 48f,
        icon = R.drawable.whiskey,
    ),
    LONG_DRINK(
        displayName = "Long drink",
        alcohol = 48f,
        icon = R.drawable.ic_cocktail,
    ),
    GIN(
        displayName = "Gin",
        alcohol = 48f,
        icon = R.drawable.ic_cocktail,
    ),
    RUM(
        displayName = "Rum",
        alcohol = 48f,
        icon = R.drawable.ic_cocktail,
    ),
    VODKA(
        displayName = "Vodka",
        alcohol = 48f,
        icon = R.drawable.vodka,
    );

    companion object {
        /**
         * @param name of the beverage, such as `RUM`.
         * See [].
         */
        fun fromName(name: String?): Beverage {
            return values()
                .firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}
