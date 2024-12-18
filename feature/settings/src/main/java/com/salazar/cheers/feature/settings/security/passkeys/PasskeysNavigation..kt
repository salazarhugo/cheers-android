package com.salazar.cheers.feature.settings.security.passkeys

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object PasskeysScreen

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/passkeys"

fun NavController.navigateToPasskeys(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = PasskeysScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.passkeysScreen(
    navigateBack: () -> Unit,
) {
    composable<PasskeysScreen>(
        deepLinks = listOf(
            navDeepLink<PasskeysScreen>(basePath = DEEP_LINK_URI_PATTERN)
        ),
    ) {
        PasskeysRoute(
            navigateBack = navigateBack,
        )
    }
}
