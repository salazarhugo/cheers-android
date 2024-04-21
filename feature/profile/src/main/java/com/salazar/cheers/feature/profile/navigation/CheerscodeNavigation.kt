package com.salazar.cheers.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.profile.cheerscode.CheerscodeRoute
import com.salazar.cheers.feature.profile.profile.ProfileRoute

const val cheersCodeNavigationRoute = "cheerscode_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://cheers.social/cheerscode"

fun NavController.navigateToCheerscode(navOptions: NavOptions? = null) {
    this.navigate(cheersCodeNavigationRoute, navOptions)
}

fun NavGraphBuilder.cheersCodeScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = cheersCodeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        CheerscodeRoute(
            onBackPressed = navigateBack,
        )
    }
}