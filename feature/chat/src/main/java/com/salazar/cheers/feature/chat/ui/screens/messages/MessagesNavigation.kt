package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val messagesNavigationRoute = "messages_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/messages"

fun NavController.navigateToMessages(navOptions: NavOptions? = null) {
    this.navigate(messagesNavigationRoute, navOptions)
}

fun NavGraphBuilder.messagesScreen(
    navigateBack: () -> Unit,
    navigateToChatCamera: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
    navigateToNewChat: () -> Unit,
) {
    composable(
        route = messagesNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        MessagesRoute(
            navigateBack = navigateBack,
            navigateToChatCamera = navigateToChatCamera,
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToChatWithChannelId = navigateToChatWithChannelId,
            navigateToNewChat = navigateToNewChat,
        )
    }
}
