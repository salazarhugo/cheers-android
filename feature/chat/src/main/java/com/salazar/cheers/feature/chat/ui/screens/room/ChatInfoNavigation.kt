package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatRoute

const val CHAT_ID = "channel_id"
const val chatInfoNavigationRoute = "chat_info/{$CHAT_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/chat/$CHAT_ID/info"

fun NavController.navigateToChatInfo(
    channelId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate("chat_info/$channelId", navOptions)
}

fun NavGraphBuilder.chatInfoScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable(
        route = chatInfoNavigationRoute,
        deepLinks = listOf(
            navDeepLink {
                uriPattern =
                    DEEP_LINK_URI_PATTERN
            },
        ),
    ) {
        RoomRoute(
            navigateBack = navigateBack,
            navigateToUserProfile = navigateToOtherProfile,
            showSnackBar = {},
        )
    }
}

