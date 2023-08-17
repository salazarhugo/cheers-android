package com.softimpact.feature.passcode.settings

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
    navigateToSetPasscode: () -> Unit,
) {
    composable(
        route = passcodeSettingsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        PasscodeLockSettingRoute(
            navigateBack = navigateBack,
            navigateToSetPasscode = navigateToSetPasscode,
        )
    }
}
