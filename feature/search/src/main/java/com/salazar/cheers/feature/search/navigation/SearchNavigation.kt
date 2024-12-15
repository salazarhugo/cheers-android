package com.salazar.cheers.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable
data object Search

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/search"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(
        route = Search,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.searchScreen(
    navigateToOtherProfile: (String) -> Unit,
    navigateToMap: () -> Unit,
    navigateToParty: (partyID: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<Search>(
        deepLinks = listOf(
            navDeepLink<Search>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        SearchRoute(
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToMap = navigateToMap,
            onBackPressed = onBackPressed,
            navigateToParty = navigateToParty,
        )
    }
}