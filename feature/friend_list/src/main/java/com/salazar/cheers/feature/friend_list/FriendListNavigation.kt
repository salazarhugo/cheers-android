package com.salazar.cheers.feature.friend_list

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val friendListNavigationRoute = "friend_list_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://mobile.soft-impact.com/friend_list"

fun NavController.navigateToFriendList(navOptions: NavOptions? = null) {
    this.navigate(friendListNavigationRoute, navOptions)
}

fun NavGraphBuilder.friendListScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable(
        route = friendListNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        FriendListRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
        )
    }
}
