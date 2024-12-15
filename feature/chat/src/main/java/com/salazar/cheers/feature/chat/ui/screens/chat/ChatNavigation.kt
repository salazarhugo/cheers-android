package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/chat"

@Serializable
data class ChatScreen(
    val userID: String? = null,
    val channelID: String? = null,
)

fun NavController.navigateToChatWithChannelId(
    channelId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = ChatScreen(channelID = channelId),
        navOptions = navOptions,
    )
}

fun NavController.navigateToChatWithUserId(
    userId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = ChatScreen(userID = userId),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.chatScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToRoomDetails: (String) -> Unit,
) {
    composable<ChatScreen>(
        deepLinks = listOf(
            navDeepLink<ChatScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        ChatRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
            showSnackBar = {},
            navigateToRoomDetails = navigateToRoomDetails,
        )
    }
}
