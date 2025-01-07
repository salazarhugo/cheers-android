package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object CreateChatScreen

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/chat/create"

fun NavController.navigateToCreateChat(navOptions: NavOptions? = null) {
    this.navigate(
        route = CreateChatScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.createChatScreen(
    navigateBack: () -> Unit,
    navigateToChat: (UserItem) -> Unit,
) {
    composable<CreateChatScreen>(
        deepLinks = listOf(
            navDeepLink<CreateChatScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        CreateChatRoute(
            navigateBack = navigateBack,
            navigateToChat = navigateToChat,
        )
    }
}
