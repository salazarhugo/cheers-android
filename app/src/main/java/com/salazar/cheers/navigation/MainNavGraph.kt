package com.salazar.cheers.navigation

import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.bottomSheet
import com.salazar.cheers.Settings
import com.salazar.cheers.auth.ui.components.delete_account.DeleteAccountDialog
import com.salazar.cheers.core.ui.theme.CheersTheme
import com.salazar.cheers.core.ui.ui.MainDestinations
import com.salazar.cheers.core.util.Constants.URI
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.feature.chat.ui.chats.ChatsSheetViewModel
import com.salazar.cheers.feature.chat.ui.screens.chat.chatScreen
import com.salazar.cheers.feature.chat.ui.screens.chat.navigateToChatWithChannelId
import com.salazar.cheers.feature.chat.ui.screens.chat.navigateToChatWithUserItem
import com.salazar.cheers.feature.chat.ui.screens.create_chat.createChatScreen
import com.salazar.cheers.feature.chat.ui.screens.create_chat.navigateToCreateChat
import com.salazar.cheers.feature.chat.ui.screens.messages.messagesScreen
import com.salazar.cheers.feature.chat.ui.screens.messages.navigateToMessages
import com.salazar.cheers.feature.chat.ui.screens.room.chatInfoScreen
import com.salazar.cheers.feature.chat.ui.screens.room.navigateToChatInfo
import com.salazar.cheers.feature.comment.comment_more.CommentMoreRoute
import com.salazar.cheers.feature.comment.comments.navigateToPostComments
import com.salazar.cheers.feature.comment.comments.postCommentsScreen
import com.salazar.cheers.feature.comment.delete.DeleteCommentDialog
import com.salazar.cheers.feature.comment.replies.navigateToReplies
import com.salazar.cheers.feature.comment.replies.repliesScreen
import com.salazar.cheers.feature.create_note.createNoteScreen
import com.salazar.cheers.feature.create_note.navigateToCreateNote
import com.salazar.cheers.feature.create_post.createPostScreen
import com.salazar.cheers.feature.create_post.navigateToCreatePost
import com.salazar.cheers.feature.edit_profile.navigation.editProfileGraph
import com.salazar.cheers.feature.edit_profile.navigation.navigateToEditProfile
import com.salazar.cheers.feature.friend_list.friendListScreen
import com.salazar.cheers.feature.friend_list.navigateToFriendList
import com.salazar.cheers.feature.friend_request.friendRequestsScreen
import com.salazar.cheers.feature.friend_request.navigateToFriendRequests
import com.salazar.cheers.feature.home.home.Home
import com.salazar.cheers.feature.home.home.homeScreen
import com.salazar.cheers.feature.map.navigation.mapPostHistoryScreen
import com.salazar.cheers.feature.map.navigation.mapScreen
import com.salazar.cheers.feature.map.navigation.mapSettingsScreen
import com.salazar.cheers.feature.map.navigation.navigateToMap
import com.salazar.cheers.feature.map.navigation.navigateToMapPostHistory
import com.salazar.cheers.feature.map.navigation.navigateToMapSettings
import com.salazar.cheers.feature.notifications.navigation.navigateToNotifications
import com.salazar.cheers.feature.notifications.navigation.notificationsScreen
import com.salazar.cheers.feature.parties.detail.navigateToPartyDetail
import com.salazar.cheers.feature.parties.detail.partyDetailScreen
import com.salazar.cheers.feature.parties.navigateToParties
import com.salazar.cheers.feature.parties.partiesScreen
import com.salazar.cheers.feature.post_likes.navigateToPostLikes
import com.salazar.cheers.feature.post_likes.postLikesScreen
import com.salazar.cheers.feature.profile.ProfileStatsRoute
import com.salazar.cheers.feature.profile.ProfileStatsViewModel
import com.salazar.cheers.feature.profile.navigation.cheersCodeScreen
import com.salazar.cheers.feature.profile.navigation.navigateToCheerscode
import com.salazar.cheers.feature.profile.navigation.navigateToOtherProfile
import com.salazar.cheers.feature.profile.navigation.otherProfileScreen
import com.salazar.cheers.feature.profile.navigation.profileScreen
import com.salazar.cheers.feature.profile.other_profile.navigateToOtherProfileStats
import com.salazar.cheers.feature.profile.other_profile.otherProfileStatsScreen
import com.salazar.cheers.feature.search.navigation.navigateToSearch
import com.salazar.cheers.feature.search.navigation.searchScreen
import com.salazar.cheers.feature.settings.navigateToSettings
import com.salazar.cheers.feature.signin.navigateToSignIn
import com.salazar.cheers.feature.signup.navigateToSignUp
import com.salazar.cheers.feature.ticket.details.navigateToTicketDetails
import com.salazar.cheers.feature.ticket.details.ticketDetailsScreen
import com.salazar.cheers.feature.ticket.navigateToTickets
import com.salazar.cheers.feature.ticket.ticketsScreen
import com.salazar.cheers.friendship.ui.manage_friendship.ManageFriendshipRoute
import com.salazar.cheers.friendship.ui.manage_friendship.RemoveFriendDialog
import com.salazar.cheers.shared.util.result.getOrNull
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.ui.compose.sheets.StorySheetUIAction
import com.salazar.cheers.ui.main.camera.ChatCameraRoute
import com.salazar.cheers.ui.main.camera.cameraScreen
import com.salazar.cheers.ui.main.camera.navigateToCamera
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.nfc.NfcRoute
import com.salazar.cheers.ui.main.party.EventMoreBottomSheet
import com.salazar.cheers.ui.main.party.EventMoreSheetViewModel
import com.salazar.cheers.ui.main.party.create.createPartyGraph
import com.salazar.cheers.ui.main.party.create.navigateToCreateParty
import com.salazar.cheers.ui.main.party.guestlist.guestListScreen
import com.salazar.cheers.ui.main.party.guestlist.navigateToGuestList
import com.salazar.cheers.ui.main.share.ShareRoute
import com.salazar.cheers.ui.main.stats.DrinkingStatsRoute
import com.salazar.cheers.ui.main.story.StoryRoute
import com.salazar.cheers.ui.main.story.feed.StoryFeedRoute
import com.salazar.cheers.ui.main.story.stats.StoryStatsRoute
import com.salazar.cheers.ui.main.ticketing.TicketingRoute
import com.salazar.cheers.ui.sheets.DeleteStoryDialog
import com.salazar.cheers.ui.sheets.SendGiftRoute
import com.salazar.cheers.ui.sheets.deletePostDialog
import com.salazar.cheers.ui.sheets.navigateToDeletePostDialog
import com.salazar.cheers.ui.sheets.post_more.PostMoreRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Serializable
data object MainNavGraph

fun NavGraphBuilder.mainNavGraph(
    appSettings: Settings,
    appState: CheersAppState,
) {
    val navController = appState.navController

    navigation<MainNavGraph>(
        startDestination = Home,
    ) {
        friendRequestsScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        bottomSheet(
            route = "${MainDestinations.SHARE_ROUTE}/{partyId}",
        ) {
            ShareRoute(
                appState = appState,
                navActions = appState.navActions,
            )
        }

        ticketsScreen(
            navigateBack = navController::popBackStack,
            navigateToTicketDetails = navController::navigateToTicketDetails,
        )

        ticketDetailsScreen(
            navigateBack = navController::popBackStack,
        )

        composable(
            route = MainDestinations.NFC_ROUTE,
        ) {
            NfcRoute(
                navActions = appState.navActions
            )
        }

        notificationsScreen(
            navigateBack = navController::popBackStack,
            navigateToPostDetail = {},
            navigateToFriendRequests = navController::navigateToFriendRequests,
            navigateToComments = {},
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        bottomSheet(
            route = "${MainDestinations.SEND_GIFT_SHEET}/{receiverId}",
        ) {
            SendGiftRoute(
                navActions = appState.navActions,
                bottomSheetNavigator = appState.bottomSheetNavigator,
            )
        }

        composable(
            route = "${MainDestinations.STORY_FEED_ROUTE}/{page}",
            arguments = listOf(navArgument("page") { type = NavType.IntType }),
        ) {
            StoryFeedRoute(
                appState = appState,
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.STORY_ROUTE}?username={username}",
            arguments = listOf(navArgument("username") { nullable = true }),
        ) {
            CheersTheme(darkTheme = true) {
                StoryRoute(
                    navActions = appState.navActions,
                    bottomSheetNavigator = appState.bottomSheetNavigator,
                )
            }
        }

        createPartyGraph(
            navController = navController,
            navigateBack = navController::popBackStack,
        )

        createNoteScreen(
            navigateBack = navController::popBackStack,
        )

        createPostScreen(
            navigateBack = navController::popBackStack,
            navigateToCamera = {},
        )

        partiesScreen(
            navigateBack = navController::popBackStack,
            navigateToPartyMoreSheet = {},
            navigateToPartyDetail = navController::navigateToPartyDetail,
            navigateToTickets = navController::navigateToTickets,
            navigateToCreateParty = {
                navController.navigate(MainDestinations.CREATE_PARTY_ROUTE)
            }
        )

        dialog(
            route = "${MainDestinations.DIALOG_DELETE_STORY}/{storyID}",
        ) {
            DeleteStoryDialog(
                appState = appState,
                navActions = appState.navActions
            )
        }

        deletePostDialog(
            onBackPressed = navController::popBackStack,
        )

        homeScreen(
            appSettings = appSettings,
            onActivityClick = navController::navigateToNotifications,
            navigateToParties = navController::navigateToParties,
            onPostClick = {},
            navigateToSearch = navController::navigateToSearch,
            navigateToCreatePost = navController::navigateToCreatePost,
            navigateToCreateNote = navController::navigateToCreateNote,
            navigateToUser = navController::navigateToOtherProfile,
            navigateToMessages = navController::navigateToMessages,
            navigateToPostMoreSheet = { postID ->
                navController.navigate("${MainDestinations.POST_MORE_SHEET}/$postID") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToPostComments = navController::navigateToPostComments,
            navigateToPostLikes = navController::navigateToPostLikes,
            navigateToSignIn = navController::navigateToSignIn,
            navigateToCamera = navController::navigateToCamera,
            navigateToDeletePostDialog = navController::navigateToDeletePostDialog,
            navigateToPartyDetail = navController::navigateToPartyDetail,
            navigateToCreateParty = navController::navigateToCreateParty,
            navigateToMap = navController::navigateToMap,
        )

        mapScreen(
            navigateBack = navController::popBackStack,
            navigateToMapSettings = navController::navigateToMapSettings,
            navigateToCreatePost = {},
            navigateToChatWithUserId = navController::navigateToChatWithUserItem,
        )

        mapPostHistoryScreen(
            navigateBack = navController::popBackStack,
            navigateToMapSettings = navController::navigateToMapSettings,
            navigateToCreatePost = {},
        )

        mapSettingsScreen(
            navigateBack = navController::popBackStack,
        )


        bottomSheet("${MainDestinations.CHAT_CAMERA_ROUTE}/{roomId}") {
            ChatCameraRoute(
                navActions = appState.navActions
            )
        }

        cameraScreen(
            navigateBack = navController::popBackStack,
        )

        composable("${MainDestinations.TICKETING_ROUTE}/{eventId}") {
            TicketingRoute(
                navActions = appState.navActions,
            )
        }

        searchScreen(
            navigateToOtherProfile = navController::navigateToOtherProfile,
            navigateToMap = navController::navigateToMap,
            onBackPressed = navController::popBackStack,
            navigateToParty = navController::navigateToPartyDetail,
        )

        postCommentsScreen(
            navigateBack = navController::popBackStack,
            navigateToCommentMoreSheet = {},
            navigateToCommentReplies = navController::navigateToReplies,
            navigateToUser = navController::navigateToOtherProfile,
        )

        postLikesScreen(
            navigateBack = navController::popBackStack,
            navigateToProfile = navController::navigateToOtherProfile,
        )

        repliesScreen(
            navigateBack = navController::popBackStack,
            navigateToCommentMoreSheet = {},
            navigateToUser = navController::navigateToOtherProfile,
        )
//            deepLinks = listOf(navDeepLink { uriPattern = "$URI/comments/{commentId}/replies" }),

        bottomSheet(
            route = "${MainDestinations.COMMENT_MORE_SHEET}/{commentID}",
            arguments = listOf(
                navArgument("commentID") { nullable = false },
            )
        ) {
            CommentMoreRoute(
                navActions = appState.navActions,
            )
        }

        dialog(
            route = MainDestinations.ACCOUNT_DELETE,
        ) {
            DeleteAccountDialog(
                navigateBack = navController::popBackStack,
                navigateToSignIn = navController::navigateToSignIn,
            )
        }

        dialog(
            route = "${MainDestinations.COMMENT_DELETE}/{commentID}",
        ) {
            DeleteCommentDialog(
                navActions = appState.navActions
            )
        }

        composable(
            route = "${MainDestinations.DRINKING_STATS}/{username}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/stats/{username}" })
        ) {
            DrinkingStatsRoute(
                navActions = appState.navActions,
            )
        }

        otherProfileScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfileStats = { user ->
                navController.navigateToOtherProfileStats(
                    otherUserID = user.id,
                    username = user.username,
                    verified = user.verified,
                )
            },
            navigateToManageFriendship = { userID ->
                navController.navigate("${MainDestinations.MANAGE_FRIENDSHIP_SHEET}/$userID") {
                    launchSingleTop = true
                }
            },
            navigateToPostDetail = {},
            navigateToComments = {},
            navigateToChat = navController::navigateToChatWithUserItem,
        )

        partyDetailScreen(
            navigateBack = navController::popBackStack,
            navigateToTicketing = {},
            navigateToMap = navController::navigateToMap,
            navigateToGuestList = navController::navigateToGuestList,
            navigateToEditParty = {},
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        guestListScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        composable(
            route = "${MainDestinations.STORY_STATS_ROUTE}/{storyId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/p/{storyId}" })
        ) {
            StoryStatsRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.POST_DETAIL_ROUTE}/{postId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/p/{postId}" })
        ) {
            PostDetailRoute(
                navActions = appState.navActions,
            )
        }

        otherProfileStatsScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        composable(
            route = "${MainDestinations.PROFILE_STATS_ROUTE}/{username}/{verified}",
            arguments = listOf(
                navArgument("username") { nullable = false },
                navArgument("verified") { defaultValue = false }
            )
        ) {
            val profileStatsViewModel = hiltViewModel<ProfileStatsViewModel>()

            val username = it.arguments?.getString("username")!!
            val verified = it.arguments?.getBoolean("verified")!!

            ProfileStatsRoute(
                profileStatsViewModel = profileStatsViewModel,
                navActions = appState.navActions,
                username = username,
                verified = verified,
            )
        }

        messagesScreen(
            navigateBack = navController::popBackStack,
            navigateToChatCamera = {},
            navigateToOtherProfile = navController::navigateToOtherProfile,
            navigateToChatWithChannelId = navController::navigateToChatWithChannelId,
            navigateToNewChat = navController::navigateToCreateChat,
        )

        createChatScreen(
            navigateBack = navController::popBackStack,
            navigateToChatWithChannelId = navController::navigateToChatWithChannelId,
        )

        chatScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
            navigateToRoomDetails = navController::navigateToChatInfo,
        )

        chatInfoScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        editProfileGraph(
            navController = navController,
            navigateBack = navController::popBackStack,
        )

        profileScreen(
            navigateBack = navController::popBackStack,
            navigateToSignIn = navController::navigateToSignIn,
            navigateToSignUp = navController::navigateToSignUp,
            navigateToEditProfile = navController::navigateToEditProfile,
            navigateToOtherProfile = navController::navigateToOtherProfile,
            navigateToFriendList = navController::navigateToFriendList,
            navigateToPostDetails = { postID ->
                navController.navigate(route = "${MainDestinations.POST_DETAIL_ROUTE}/$postID")
            },
            navigateToPostMore = {
                navController.navigate("${MainDestinations.POST_MORE_SHEET}/$it")
            },
            navigateToMapPostHistory = navController::navigateToMapPostHistory,
            navigateToCheerscode = navController::navigateToCheerscode,
            navigateToNfc = {},
            navigateToSettings = navController::navigateToSettings,
        )

        cheersCodeScreen(
            navigateBack = navController::popBackStack,
        )

        friendListScreen(
            navigateBack = navController::popBackStack,
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        bottomSheet(
            route = "${MainDestinations.STORY_MORE_SHEET}/{storyID}",
        ) {
            val storyID = it.arguments?.getString("storyID")!!

            StoryMoreBottomSheet(onStorySheetUIAction = { action ->
                when (action) {
                    StorySheetUIAction.OnAddSnapchatFriends -> {}
                    StorySheetUIAction.OnCopyStoryClick -> {}
                    StorySheetUIAction.OnNfcClick -> {}
                    StorySheetUIAction.OnPostHistoryClick -> {}
                    StorySheetUIAction.OnSettingsClick -> {}
                    StorySheetUIAction.OnDeleteClick -> {
                        appState.navActions.navigateToDeleteStoryDialog(storyID)
                        appState.navActions.navigateBack()
                    }
                }
            })
        }

        dialog(
            route = "${MainDestinations.DIALOG_REMOVE_FRIEND}/{friendId}",
        ) {
            RemoveFriendDialog(
                navActions = appState.navActions
            )
        }

        bottomSheet(
            route = "${MainDestinations.MANAGE_FRIENDSHIP_SHEET}/{friendId}",
            arguments = listOf(
                navArgument("friendId") { nullable = false },
            )
        ) {
            ManageFriendshipRoute(
                navActions = appState.navActions,
            )
        }

        bottomSheet(
            route = "${MainDestinations.POST_MORE_SHEET}/{postID}",
            arguments = listOf(
                navArgument("postID") { nullable = false },
            )
        ) {
            PostMoreRoute(
                navActions = appState.navActions,
            )
        }

        bottomSheet("${MainDestinations.EVENT_MORE_SHEET}/{eventId}") {
            val eventId = it.arguments?.getString("eventId")!!
            val context = LocalContext.current
            val viewModel = hiltViewModel<EventMoreSheetViewModel>()
            val clipboardManager = LocalClipboardManager.current
            val scope = rememberCoroutineScope()

            EventMoreBottomSheet(
                modifier = Modifier.navigationBarsPadding(),
                isAuthor = false,
                onDetails = { navController.navigateToPartyDetail(eventId) },
                onDelete = { },
                onReport = { /*TODO*/ },
                onShare = {
                    scope.launch {
                        val link =
                            FirebaseDynamicLinksUtil.createShortLink("event/$eventId").getOrNull()
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, link)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                        appState.navActions.navigateBack()
                    }
                },
                onLinkClick = {
                    scope.launch {
                        val link =
                            FirebaseDynamicLinksUtil.createShortLink("event/$eventId").getOrNull()
                                ?: return@launch
                        clipboardManager.setText(AnnotatedString(link))
                        appState.navActions.navigateBack()
                    }
                },
                onHide = {
                    viewModel.onHide()
                    appState.navActions.navigateBack()
                },
            )
        }
    }


    bottomSheet(
        route = "${MainDestinations.MESSAGES_MORE_SHEET}/{channelId}",
    ) {
        val chatsSheetViewModel = hiltViewModel<ChatsSheetViewModel>()

        val uiState by chatsSheetViewModel.uiState.collectAsStateWithLifecycle()
    }
}