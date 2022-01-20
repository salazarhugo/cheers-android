package com.salazar.cheers

import androidx.navigation.NavHostController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Destinations used in the [CheersApp].
 */
object CheersDestinations {
    const val HOME_ROUTE = "home"
    const val PROFILE_ROUTE = "profile"
    const val EDIT_PROFILE_ROUTE = "editProfile"
    const val PROFILE_STATS_ROUTE = "profileStats"
    const val MAP_ROUTE = "map"
    const val SEARCH_ROUTE = "search"
    const val MESSAGES_ROUTE = "messages"
    const val CAMERA_ROUTE = "camera"
    const val OTHER_PROFILE_ROUTE = "otherProfile"
    const val ACTIVITY_ROUTE = "activity"
    const val LIKES_ROUTE = "likes"
    const val CHAT_ROUTE = "chat"
    const val SETTINGS_ROUTE = "settings"
    const val POST_DETAIL_ROUTE = "postDetail"
    const val EVENT_DETAIL_ROUTE = "eventDetail"
    const val ADD_POST_SHEET = "addPostSheet"
    const val PROFILE_MORE_SHEET = "profileMoreSheet"
    const val POST_MORE_SHEET = "postMoreSheet"
}

/**
 * Models the navigation actions in the app.
 */
class CheersNavigationActions(navController: NavHostController) {

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val navigateToEditProfile: () -> Unit = {
        navController.navigate(CheersDestinations.EDIT_PROFILE_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAddPostSheetWithPhotoUri: (photoUri: String) -> Unit = { photoUri ->
        navController.navigate("${CheersDestinations.ADD_POST_SHEET}?photoUri=$photoUri") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToPostMoreSheet: (postId: String, isAuthor: Boolean) -> Unit = { postId, isAuthor ->
        navController.navigate("${CheersDestinations.POST_MORE_SHEET}/$postId/$isAuthor") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToProfileMoreSheet: () -> Unit = {
        navController.navigate(CheersDestinations.PROFILE_MORE_SHEET) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAddPostSheet: () -> Unit = {
        navController.navigate(CheersDestinations.ADD_POST_SHEET) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToHome: () -> Unit = {
        navController.navigate(CheersDestinations.HOME_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
//            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToMap: () -> Unit = {
        navController.navigate(CheersDestinations.MAP_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSearch: () -> Unit = {
        navController.navigate(CheersDestinations.SEARCH_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToMessages: () -> Unit = {
        navController.navigate(CheersDestinations.MESSAGES_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToProfile: () -> Unit = {
        navController.navigate(CheersDestinations.PROFILE_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToActivity: () -> Unit = {
        navController.navigate(CheersDestinations.ACTIVITY_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToOtherProfile: (username: String) -> Unit = { username ->
        navController.navigate("${CheersDestinations.OTHER_PROFILE_ROUTE}/$username") {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToPostDetail: (postId: String) -> Unit = { postId ->
        navController.navigate("${CheersDestinations.POST_DETAIL_ROUTE}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToLikes: (postId: String) -> Unit = { postId ->
        navController.navigate("${CheersDestinations.LIKES_ROUTE}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(CheersDestinations.SETTINGS_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToChat: (
        channelId: String,
        username: String,
        name: String,
        profilePictureUrl: String
    ) -> Unit = { channelId, username, name, profilePictureUrl ->
        val encodedUrl = URLEncoder.encode(profilePictureUrl, StandardCharsets.UTF_8.toString())
        navController.navigate("${CheersDestinations.CHAT_ROUTE}/$channelId/$username/$name/$encodedUrl") {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToProfileStats: (statName: String, username: String) -> Unit = { s, u ->
        navController.navigate(CheersDestinations.PROFILE_STATS_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToCamera: () -> Unit = {
        navController.navigate(CheersDestinations.CAMERA_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }
}