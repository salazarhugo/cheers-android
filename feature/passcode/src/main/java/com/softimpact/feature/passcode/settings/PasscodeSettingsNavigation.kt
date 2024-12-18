package com.softimpact.feature.passcode.settings

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val passcodeSettingsNavigationRoute = "passcode_settings_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/passcode_settings"

fun NavController.navigateToPasscodeSettings(navOptions: NavOptions? = null) {
    this.navigate("passcode_settings_route", navOptions)
}

fun NavGraphBuilder.passcodeSettingsScreen(
    navigateBack: () -> Unit,
    navigateToChangePasscode: () -> Unit,
) {
    composable(
        route = passcodeSettingsNavigationRoute,
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
        PasscodeSettingRoute(
            navigateBack = navigateBack,
            navigateToChangePasscode = navigateToChangePasscode,
        )
    }
}
