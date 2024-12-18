package com.salazar.cheers.feature.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.feature.map.screens.map.MapRoute

const val mapNavigationRoute = "map_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/map"

fun NavController.navigateToMap(navOptions: NavOptions? = null) {
    this.navigate(mapNavigationRoute, navOptions)
}

fun NavGraphBuilder.mapScreen(
    navigateBack: () -> Unit,
    navigateToMapSettings: () -> Unit,
    navigateToCreatePost: () -> Unit,
    navigateToChatWithUserId: (UserItem) -> Unit,
) {
    composable(
        route = mapNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        MapRoute(
            navigateBack = navigateBack,
            navigateToCreatePost = navigateToCreatePost,
            navigateToMapSettings = navigateToMapSettings,
            navigateToChatWithUserId = {},
        )
    }
}