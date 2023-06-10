package com.salazar.cheers.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.search.SearchRoute

const val searchNavigationRoute = "search_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://searcharty.app/search"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(searchNavigationRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(
    navigateToOtherProfile: (String) -> Unit,
) {
    composable(
        route = searchNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SearchRoute(
            navigateToOtherProfile = navigateToOtherProfile,
        )
    }
}