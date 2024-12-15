package com.salazar.cheers.core.ui.ui

import androidx.navigation.NavHostController

/**
 * Destinations used in the [CheersApp].
 */
object CheersDestinations {
    const val AUTH_ROUTE = "auth"
    const val MAIN_ROUTE = "main"
}

/**
 * Destinations used in the [MainGraph].
 */
object MainDestinations {
    const val ROOM_DETAILS = "roomDetails"
    const val DIALOG_DELETE_POST = "dialogDeletePost"
    const val DIALOG_DELETE_STORY = "dialogDeleteStory"
    const val EDIT_EVENT_ROUTE = "event/edit"
    const val EVENT_MORE_SHEET = "eventMoreSheet"
    const val MESSAGES_MORE_SHEET = "messagesMoreSheet"
    const val NEW_CHAT_ROUTE = "newChat"
    const val TICKETING_ROUTE = "ticketing"
    const val EDIT_PROFILE_ROUTE = "editProfile"
    const val PROFILE_STATS_ROUTE = "profileStats"
    const val OTHER_PROFILE_STATS_ROUTE = "otherProfileStats"
    const val POST_COMMENTS = "comments"
    const val COMMENT_REPLIES = "replies"
    const val COMMENT_MORE_SHEET = "commentMore"
    const val COMMENT_DELETE = "deleteComment"
    const val ACCOUNT_DELETE = "deleteAccount"
    const val CAMERA_ROUTE = "camera"
    const val CHAT_CAMERA_ROUTE = "chatCamera"
    const val LIKES_ROUTE = "likes"
    const val STORY_STATS_ROUTE = "storyStats"
    const val CHAT_ROUTE = "chat"
    const val POST_DETAIL_ROUTE = "postDetail"
    const val GUEST_LIST_ROUTE = "guestList"
    const val CREATE_PARTY_ROUTE = "create_party_route"
    const val POST_MORE_SHEET = "postMoreSheet"
    const val STORY_MORE_SHEET = "storyMoreSheet"
    const val STORY_ROUTE = "story"
    const val STORY_FEED_ROUTE = "storyFeed"
    const val SEND_GIFT_SHEET = "giftSheet"
    const val DRINKING_STATS = "drinkingStats"
    const val NFC_ROUTE = "nfc"
    const val SHARE_ROUTE = "share"
    const val MANAGE_FRIENDSHIP_SHEET = "manageFriendship"
    const val NOTE_SHEET = "note"
    const val DIALOG_REMOVE_FRIEND = "dialogRemoveFriend"
}

/**
 * Destinations used in the [AuthGraph].
 */
object AuthDestinations {
    const val SIGN_IN_ROUTE = "signIn"
    const val SIGN_UP_ROUTE = "signUp"
    const val REGISTER_ROUTE = "register"
}

/**
 * Destinations used in [Settings].
 */
object SettingDestinations {
    const val THEME_ROUTE = "theme"
    const val NOTIFICATIONS_ROUTE = "notifications"
    const val LANGUAGE_ROUTE = "language"
    const val CHAT_SETTINGS_ROUTE = "chatSettings"
    const val ADD_PAYMENT_METHOD_ROUTE = "addPaymentMethod"
    const val RECHARGE_ROUTE = "recharge"
    const val PAYMENT_HISTORY_ROUTE = "paymentHistory"
    const val SECURITY_ROUTE = "security"
    const val PASSWORD_ROUTE = "password"
}

/**
 * Models the navigation actions in the app.
 */
class CheersNavigationActions(
    private val navController: NavHostController,
) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val navigateToRemoveFriendDialog: (friendId: String) -> Unit = { postId ->
        navController.navigate("${MainDestinations.DIALOG_REMOVE_FRIEND}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToNote: (String) -> Unit = { userID ->
        navController.navigate("${MainDestinations.NOTE_SHEET}/$userID") {
            launchSingleTop = true
        }
    }

    val navigateToManageFriendship: (String) -> Unit = { friendId ->
        navController.navigate("${MainDestinations.MANAGE_FRIENDSHIP_SHEET}/$friendId") {
            launchSingleTop = true
        }
    }

    val navigateToRegister: () -> Unit = {
        navController.navigate("${AuthDestinations.REGISTER_ROUTE}/emailLink") {
            launchSingleTop = true
        }
    }

    val navigateToPassword: (Boolean) -> Unit = { hasPassword ->
        navController.navigate("${SettingDestinations.PASSWORD_ROUTE}/$hasPassword") {
            launchSingleTop = true
        }
    }

    val navigateToTicketing: (String) -> Unit = { eventId ->
        navController.navigate("${MainDestinations.TICKETING_ROUTE}/$eventId") {
            launchSingleTop = true
        }
    }

    val navigateToSecurity = {
        navController.navigate(SettingDestinations.SECURITY_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToEventMoreSheet: (String) -> Unit = { eventId ->
        navController.navigate("${MainDestinations.EVENT_MORE_SHEET}/$eventId") {
            launchSingleTop = true
        }
    }

    val navigateToEditEvent: (String) -> Unit = { eventId ->
        navController.navigate("${MainDestinations.EDIT_EVENT_ROUTE}/$eventId") {
            launchSingleTop = true
        }
    }

    val navigateToRoomDetails: (String) -> Unit = { roomId ->
        navController.navigate("${MainDestinations.ROOM_DETAILS}/$roomId") {
            launchSingleTop = true
        }
    }

    val navigateToNewChat: () -> Unit = {
        navController.navigate(route = MainDestinations.NEW_CHAT_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToNfc: () -> Unit = {
        navController.navigate(route = MainDestinations.NFC_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToSendGift: (String) -> Unit = { receiverId ->
        navController.navigate(route = "${MainDestinations.SEND_GIFT_SHEET}/$receiverId") {
            launchSingleTop = true
        }
    }

    val navigateToStoryWithUserId: (username: String) -> Unit = { username ->
        navController.navigate(route = "${MainDestinations.STORY_ROUTE}?username=$username") {
            launchSingleTop = true
        }
    }

    val navigateToStoryFeed: (page: Int) -> Unit = { page ->
        navController.navigate(route = "${MainDestinations.STORY_FEED_ROUTE}/$page") {
            launchSingleTop = true
        }
    }

    val navigateToPaymentHistory: () -> Unit = {
        navController.navigate(SettingDestinations.PAYMENT_HISTORY_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToRecharge: () -> Unit = {
        navController.navigate(SettingDestinations.RECHARGE_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToAddPaymentMethod: () -> Unit = {
        navController.navigate(SettingDestinations.ADD_PAYMENT_METHOD_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToChatSettings: () -> Unit = {
        navController.navigate(SettingDestinations.CHAT_SETTINGS_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToLanguage: () -> Unit = {
        navController.navigate(SettingDestinations.LANGUAGE_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToNotifications: () -> Unit = {
        navController.navigate(SettingDestinations.NOTIFICATIONS_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToTheme: () -> Unit = {
        navController.navigate(SettingDestinations.THEME_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToSignUp: () -> Unit = {
        navController.navigate(AuthDestinations.SIGN_UP_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToSignUpWithGoogle: (email: String, displayName: String) -> Unit =
        { email, displayName ->
            navController.navigate("${AuthDestinations.SIGN_UP_ROUTE}?email=$email&displayName=$displayName") {
                launchSingleTop = true
            }
        }

    val navigateToMain: () -> Unit = {
        navController.navigate(CheersDestinations.MAIN_ROUTE) {
            navController.popBackStack(route = CheersDestinations.MAIN_ROUTE, inclusive = true)
            launchSingleTop = true
        }
    }

    val navigateToSignIn: () -> Unit = {
        navController.navigate(AuthDestinations.SIGN_IN_ROUTE) {
            val a = navController.popBackStack(route = AuthDestinations.SIGN_IN_ROUTE, inclusive = true)
            print(a)
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToEditProfile: () -> Unit = {
        navController.navigate(MainDestinations.EDIT_PROFILE_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAddEvent: () -> Unit = {
        navController.navigate(MainDestinations.CREATE_PARTY_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToChatsMoreSheet: (channelId: String) -> Unit = { channelId ->
        navController.navigate("${MainDestinations.MESSAGES_MORE_SHEET}/$channelId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToPostMoreSheet: (postId: String) -> Unit = { postId  ->
        navController.navigate("${MainDestinations.POST_MORE_SHEET}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToMap: () -> Unit = {
    }

    val navigateToComments: (postId: String) -> Unit = { postId ->
        navController.navigate("${MainDestinations.POST_COMMENTS}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToCommentReplies: (commentId: String) -> Unit = { commentId ->
        navController.navigate("${MainDestinations.COMMENT_REPLIES}/$commentId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToCommentMoreSheet: (commentID: String) -> Unit = { commentID ->
        navController.navigate("${MainDestinations.COMMENT_MORE_SHEET}/$commentID") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDeleteCommentDialog: (commentID: String) -> Unit = { commentID ->
        navController.navigate("${MainDestinations.COMMENT_DELETE}/$commentID") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDrinkingStats: (username: String) -> Unit = { username ->
        navController.navigate("${MainDestinations.DRINKING_STATS}/$username") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToOtherProfile: (username: String) -> Unit = { username ->
    }

    val navigateToStoryMoreSheet: (storyId: String) -> Unit = { storyId ->
        navController.navigate("${MainDestinations.STORY_MORE_SHEET}/$storyId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToStoryStats: (storyId: String) -> Unit = { storyId ->
        navController.navigate("${MainDestinations.STORY_STATS_ROUTE}/$storyId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDeleteStoryDialog: (storyId: String) -> Unit = { storyId ->
        navController.navigate("${MainDestinations.DIALOG_DELETE_STORY}/$storyId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDeletePostDialog: (postId: String) -> Unit = { postId ->
        navController.navigate("${MainDestinations.DIALOG_DELETE_POST}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToPostDetail: (postId: String) -> Unit = { postId ->
        navController.navigate("${MainDestinations.POST_DETAIL_ROUTE}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToGuestList: (eventId: String) -> Unit = { eventId ->
        navController.navigate("${MainDestinations.GUEST_LIST_ROUTE}/$eventId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToLikes: (postId: String) -> Unit = { postId ->
        navController.navigate("${MainDestinations.LIKES_ROUTE}/$postId") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToChatWithUserId: (String) -> Unit = { userID ->
        navController.navigate("${MainDestinations.CHAT_ROUTE}?userId=$userID") {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToChatWithChannelId: (String) -> Unit = { channelID ->
        navController.navigate("${MainDestinations.CHAT_ROUTE}?channelId=$channelID") {
            launchSingleTop = true
            restoreState = true
        }
    }


    val navigateToProfileStats: (statName: String, username: String, verified: Boolean) -> Unit =
        { s, username, verified ->
            navController.navigate("${MainDestinations.PROFILE_STATS_ROUTE}/$username/$verified") {
                launchSingleTop = true
                restoreState = true
            }
        }

    val navigateToChatCamera: (String) -> Unit = { roomId ->
        navController.navigate("${MainDestinations.CHAT_CAMERA_ROUTE}/$roomId") {
            launchSingleTop = true
            restoreState = true
        }
    }
}