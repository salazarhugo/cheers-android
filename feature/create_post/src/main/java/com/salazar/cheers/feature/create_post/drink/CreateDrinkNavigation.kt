package com.salazar.cheers.feature.create_post.drink

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object CreateDrinkScreen

fun NavController.navigateToCreateDrink() {
    navigate(
        route = CreateDrinkScreen,
    )
}

fun NavGraphBuilder.createDrinkScreen(
    navigateBack: () -> Unit,
) {
    composable<CreateDrinkScreen>(
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
        }
    ) { backStackEntry ->
        CreateDrinkRoute(
            navigateBack = navigateBack,
        )
    }
}
