package com.salazar.cheers.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val homeNavigationRoute = "home_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onActivityClick: () -> Unit,
    navigateToSearch: () -> Unit,
    onPostClick: (String) -> Unit,
) {
    composable(
        route = homeNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        HomeRoute(
            onActivityClick = onActivityClick,
            onPostClick = onPostClick,
        )
    }
}