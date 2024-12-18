package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data class ChatInfoScreen(
    val chatID: String,
)

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/chat/{id}/info"

fun NavController.navigateToChatInfo(
    chatID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = ChatInfoScreen(chatID = chatID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.chatInfoScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable<ChatInfoScreen>(
        deepLinks = listOf(
            navDeepLink<ChatInfoScreen>(basePath = DEEP_LINK_URI_PATTERN),
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
        RoomRoute(
            navigateBack = navigateBack,
            navigateToUserProfile = navigateToOtherProfile,
            showSnackBar = {},
        )
    }
}

