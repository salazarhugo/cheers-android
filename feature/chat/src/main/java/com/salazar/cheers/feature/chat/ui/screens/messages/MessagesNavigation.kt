package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object Messages

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/messages"

fun NavController.navigateToMessages(navOptions: NavOptions? = null) {
    this.navigate(
        route = Messages,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.messagesScreen(
    navigateBack: () -> Unit,
    navigateToChatCamera: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
    navigateToNewChat: () -> Unit,
) {
    composable<Messages>(
        deepLinks = listOf(
            navDeepLink<Messages>(basePath = DEEP_LINK_URI_PATTERN),
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
