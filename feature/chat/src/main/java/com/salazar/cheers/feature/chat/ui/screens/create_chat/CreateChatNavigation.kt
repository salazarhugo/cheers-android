package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val createChatNavigationRoute = "create_chat_route"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/create_chat"

fun NavController.navigateToCreateChat(navOptions: NavOptions? = null) {
    this.navigate(createChatNavigationRoute, navOptions)
}

fun NavGraphBuilder.createChatScreen(
    navigateBack: () -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
) {
    composable(
        route = createChatNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        CreateChatRoute(
            navigateBack = navigateBack,
            navigateToChatWithChannelId = navigateToChatWithChannelId,
        )
    }
}
