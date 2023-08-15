package com.salazar.cheers.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.settings.SettingsRoute

const val settingsNavigationRoute = "settings_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/settings"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(settingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen(
    navigateToSignIn: () -> Unit,
) {
    composable(
        route = settingsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SettingsRoute(
            navigateToAddPaymentMethod = {},
            navigateToDeleteAccount = {},
            navigateToLanguage = {},
            navigateToNotifications = {},
            navigateToPaymentHistory = {},
            navigateToRecharge = {},
            navigateToSecurity = {},
            navigateToSignIn = navigateToSignIn,
            navigateToTheme = {},
        )
    }
}
