package com.softimpact.feature.passcode.passcode

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val passcodeNavigationRoute = "passcode_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/passcode"

fun NavController.navigateToPasscode(navOptions: NavOptions? = null) {
    this.navigate(passcodeNavigationRoute, navOptions)
}

fun NavGraphBuilder.passcodeScreen(
    banner: Int,
    navigateToHome: () -> Unit,
) {
    composable(
        route = passcodeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        PasscodeRoute(
            banner = banner,
            navigateToHome = navigateToHome,
        )
    }
}
