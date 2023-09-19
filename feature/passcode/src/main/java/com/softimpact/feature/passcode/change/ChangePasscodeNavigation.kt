package com.softimpact.feature.passcode.change

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val changePasscodeNavigationRoute = "change_passcode_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/change_passcode"

fun NavController.navigateToChangePasscode(navOptions: NavOptions? = null) {
    this.navigate(changePasscodeNavigationRoute, navOptions)
}

fun NavGraphBuilder.changePasscodeScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = changePasscodeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        ChangePasscodeRoute(
            navigateBack = navigateBack,
        )
    }
}
