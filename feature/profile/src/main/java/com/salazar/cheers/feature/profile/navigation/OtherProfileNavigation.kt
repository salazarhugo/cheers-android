package com.salazar.cheers.feature.profile.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.feature.profile.other_profile.OtherProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data class OtherProfileScreen(
    val username: String,
)

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/{username}"

fun NavController.navigateToOtherProfile(
    username: String,
    navOptions: NavOptions? = null,
) {
    if (username.isBlank()) {
        return
    }
    this.navigate(
        route = OtherProfileScreen(username = username),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.otherProfileScreen(
    navigateBack: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToOtherProfileStats: (User) -> Unit,
    navigateToManageFriendship: (String) -> Unit,
    navigateToChat: (UserItem) -> Unit,
    navigateToPostLikes: (String) -> Unit,
    navigateToUserProfile: (String) -> Unit,
) {
    composable<OtherProfileScreen>(
        deepLinks = listOf(
            navDeepLink<OtherProfileScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        OtherProfileRoute(
            navigateBack = navigateBack,
            navigateToComments = navigateToComments,
            navigateToPostDetail = navigateToPostDetail,
            navigateToManageFriendship = navigateToManageFriendship,
            navigateToOtherProfileStats = navigateToOtherProfileStats,
            navigateToChat = navigateToChat,
            navigateToPostLikes = navigateToPostLikes,
            navigateToUserProfile = navigateToUserProfile,
        )
    }
}