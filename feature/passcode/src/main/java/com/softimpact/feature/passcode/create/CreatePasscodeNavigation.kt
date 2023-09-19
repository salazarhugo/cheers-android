package com.softimpact.feature.passcode.create

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
    ) {
        CreatePasscodeRoute(
            navigateBack = navigateBack,
            navigateToPasscodeSettings = navigateToPasscodeSettings,
        )
    }
}
