package com.salazar.cheers.feature.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.notifications.activity.ActivityRoute

const val notificationsNavigationRoute = "notifications_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://notificationsarty.app/notifications"

fun NavController.navigateToNotifications(navOptions: NavOptions? = null) {
    this.navigate(notificationsNavigationRoute, navOptions)
}

fun NavGraphBuilder.notificationsScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToFriendRequests: () -> Unit,
) {
    composable(
        route = notificationsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        ActivityRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToComments = navigateToComments,
            navigateToFriendRequests = navigateToFriendRequests,
            navigateToPostDetail = navigateToPostDetail,
        )
    }
}
