package com.softimpact.feature.passcode.set

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val setPasscodeNavigationRoute = "set_passcode_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/set_passcode"

fun NavController.navigateToSetPasscode(navOptions: NavOptions? = null) {
    this.navigate("set_passcode_route", navOptions)
}

fun NavGraphBuilder.setPasscodeScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(
        route = setPasscodeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SetPinLockRoute(
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
        )
    }
}
