package com.softimpact.feature.passcode.create

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val createPasscodeNavigationRoute = "create_passcode_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/create_passcode"

fun NavController.navigateToCreatePasscode(navOptions: NavOptions? = null) {
    this.navigate("create_passcode_route", navOptions)
}

fun NavGraphBuilder.createPasscodeScreen(
    navigateBack: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
) {
    composable(
        route = createPasscodeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
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
        CreatePasscodeRoute(
            navigateBack = navigateBack,
            navigateToPasscodeSettings = navigateToPasscodeSettings,
        )
    }
}
