package com.salazar.cheers.feature.drink

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DrinkScreen(
    val drinkID: String,
)

fun NavController.navigateToDrink(
    drinkID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = DrinkScreen(drinkID = drinkID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.drinkScreen(
    navigateBack: () -> Unit,
) {
    composable<DrinkScreen> {
        DrinkRoute(
            navigateBack = navigateBack,
        )
    }
}
