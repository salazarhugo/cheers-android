package com.salazar.cheers.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.model.User
import com.salazar.cheers.feature.profile.other_profile.OtherProfileRoute

const val USERNAME = "username"
const val otherProfileNavigationRoute = "other_profile_route/{$USERNAME}"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/otherProfile/{$USERNAME}"

fun NavController.navigateToOtherProfile(
    username: String,
    navOptions: NavOptions? = null,
) {
    if (username.isBlank()) {
        return
    }
    this.navigate("other_profile_route/$username", navOptions)
}

fun NavGraphBuilder.otherProfileScreen(
    navigateBack: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToOtherProfileStats: (User) -> Unit,
    navigateToManageFriendship: (String) -> Unit,
    navigateToChat: (String) -> Unit,
) {
    composable(
        route = otherProfileNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
        arguments = listOf(
            navArgument(USERNAME) { type = NavType.StringType },
        ),
    ) {
        OtherProfileRoute(
            navigateBack = navigateBack,
            navigateToComments = navigateToComments,
            navigateToPostDetail = navigateToPostDetail,
            navigateToManageFriendship = navigateToManageFriendship,
            navigateToOtherProfileStats = navigateToOtherProfileStats,
            navigateToChat = navigateToChat,
        )
    }
}