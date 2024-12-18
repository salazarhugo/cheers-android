package com.salazar.cheers.feature.settings.language

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object LanguageScreen

fun NavController.navigateToLanguage(
    navOptions: NavOptions? = null,
) {
    this.navigate(LanguageScreen, navOptions)
}

fun NavGraphBuilder.languagesScreen(
    navigateBack: () -> Unit,
) {
    composable<LanguageScreen>(
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        LanguageRoute(
            navigateBack = navigateBack,
        )
    }
}
