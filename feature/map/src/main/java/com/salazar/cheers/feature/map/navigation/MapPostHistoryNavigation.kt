package com.salazar.cheers.feature.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.map.ui.MapPostHistoryRoute

const val mapPostHistoryNavigationRoute = "map_post_history_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/map_post_history"

fun NavController.navigateToMapPostHistory(navOptions: NavOptions? = null) {
    this.navigate(mapPostHistoryNavigationRoute, navOptions)
}

fun NavGraphBuilder.mapPostHistoryScreen(
    navigateBack: () -> Unit,
    navigateToMapSettings: () -> Unit,
    navigateToCreatePost: () -> Unit,
) {
    composable(
        route = mapPostHistoryNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        MapPostHistoryRoute(
        )
    }
}