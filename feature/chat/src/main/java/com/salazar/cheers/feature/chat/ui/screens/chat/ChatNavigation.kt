package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val CHANNEL_ID = "channel_id"
const val USER_ID = "user_id"
const val chatNavigationRoute = "chat_route?$CHANNEL_ID={$CHANNEL_ID}&$USER_ID={$USER_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/chat"

fun NavController.navigateToChat(
    channelId: String?,
    userId: String? = null,
    navOptions: NavOptions? = null,
) {
    this.navigate("chat_route?$CHANNEL_ID=$channelId&$USER_ID=$userId", navOptions)
}

fun NavGraphBuilder.chatScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToRoomDetails: (String) -> Unit,
) {
    composable(
        route = chatNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
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
