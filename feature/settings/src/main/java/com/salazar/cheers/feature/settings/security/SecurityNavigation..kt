package com.salazar.cheers.feature.settings.security

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val securityNavigationRoute = "security_route"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/security_note"

fun NavController.navigateToSecurity(
    navOptions: NavOptions? = null,
) {
    this.navigate(securityNavigationRoute, navOptions)
}

fun NavGraphBuilder.securityScreen(
    navigateBack: () -> Unit,
    navigateToPassword: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
    navigateToCreatePasscode: () -> Unit,
) {
    composable(
        route = securityNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SecurityRoute(
            navigateBack = navigateBack,
            navigateToPassword = navigateToPassword,
            navigateToPasscodeSettings = navigateToPasscodeSettings,
            navigateToCreatePasscode = navigateToCreatePasscode,
        )
    }
}
