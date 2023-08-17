package com.salazar.cheers.feature.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val settingsNavigationRoute = "settings_route"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/settings_note"

fun NavController.navigateToSettings(
    navOptions: NavOptions? = null,
) {
    this.navigate(settingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToAddPaymentMethod: () -> Unit,
    navigateToLanguage: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToTheme: () -> Unit,
    navigateToRecharge: () -> Unit,
    navigateToSecurity: () -> Unit,
    navigateToPaymentHistory: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToDeleteAccount: () -> Unit,
) {
    composable(
        route = settingsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SettingsRoute(
            navigateBack = navigateBack,
            navigateToAddPaymentMethod = navigateToAddPaymentMethod,
            navigateToLanguage = navigateToLanguage,
            navigateToNotifications = navigateToNotifications,
            navigateToTheme = navigateToTheme,
            navigateToRecharge = navigateToRecharge,
            navigateToSecurity = navigateToSecurity,
            navigateToPaymentHistory = navigateToPaymentHistory,
            navigateToSignIn = navigateToSignIn,
            navigateToDeleteAccount = navigateToDeleteAccount,
        )
    }
}
