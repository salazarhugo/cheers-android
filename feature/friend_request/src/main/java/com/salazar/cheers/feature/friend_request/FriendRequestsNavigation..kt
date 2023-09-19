package com.salazar.cheers.feature.friend_request

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val friendRequestsNavigationRoute = "friend_requests_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/friend_requests"

fun NavController.navigateToFriendRequests(navOptions: NavOptions? = null) {
    this.navigate(friendRequestsNavigationRoute, navOptions)
}

fun NavGraphBuilder.friendRequestsScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable(
        route = friendRequestsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        FriendRequestsRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
        )
    }
}
