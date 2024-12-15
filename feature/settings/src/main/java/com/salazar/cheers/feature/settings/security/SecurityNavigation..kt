package com.salazar.cheers.feature.settings.security

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object SecurityScreen

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/security"

fun NavController.navigateToSecurity(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = SecurityScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.securityScreen(
    navigateBack: () -> Unit,
    navigateToPasskeys: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
    navigateToCreatePasscode: () -> Unit,
) {
    composable<SecurityScreen>(
        deepLinks = listOf(
            navDeepLink<SecurityScreen>(basePath = DEEP_LINK_URI_PATTERN)
        ),
    ) {
        SecurityRoute(
            navigateBack = navigateBack,
            navigateToPasscodeSettings = navigateToPasscodeSettings,
            navigateToCreatePasscode = navigateToCreatePasscode,
            navigateToPasskeys = navigateToPasskeys,
        )
    }
}
