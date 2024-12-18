package com.salazar.cheers.feature.chat.ui.screens.chat

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.salazar.cheers.core.model.UserItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/chat"

@Serializable
data class ChatScreen(
    val user: UserItem? = null,
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

fun NavController.navigateToChatWithUserItem(
    userItem: UserItem,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = ChatScreen(user = userItem),
        navOptions = navOptions,
    )
}

object CustomNavType {
    val UserItemType = object : NavType<UserItem?>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): UserItem? {
            val jsonString = bundle.getString(key) ?: return null
            return Json.decodeFromString(Uri.decode(jsonString))
        }

        override fun parseValue(value: String): UserItem {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: UserItem?) {
            val jsonString = Json.encodeToString(value)
            return bundle.putString(key, Uri.encode(jsonString))
        }

        override fun serializeAsValue(value: UserItem?): String {
            val jsonString = Json.encodeToString(value)
            return Uri.encode(jsonString)
        }
    }
}

fun NavGraphBuilder.chatScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToRoomDetails: (String) -> Unit,
) {
    composable<ChatScreen>(
        typeMap = mapOf(
            typeOf<UserItem?>() to CustomNavType.UserItemType,
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
        ChatRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
            showSnackBar = {},
            navigateToRoomDetails = navigateToRoomDetails,
        )
    }
}
