package com.salazar.cheers.feature.premium.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val premiumNavigationRoute = "premium_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://cheers.social/premium"

fun NavController.navigateToPremium(navOptions: NavOptions? = null) {
    this.navigate(premiumNavigationRoute, navOptions)
}

fun NavGraphBuilder.premiumScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = premiumNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        PremiumRoute(
            onBackPressed = navigateBack,
        )
    }
}