package com.salazar.cheers.navigation

import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.bottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.auth.ui.components.delete_account.DeleteAccountDialog
import com.salazar.cheers.comment.ui.comment_more.CommentMoreRoute
import com.salazar.cheers.comment.ui.comments.CommentsRoute
import com.salazar.cheers.comment.ui.delete.DeleteCommentDialog
import com.salazar.cheers.comment.ui.replies.RepliesRoute
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.theme.CheersTheme
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.MainDestinations
import com.salazar.cheers.core.util.Constants.URI
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.core.util.Utils.shareToSnapchat
import com.salazar.cheers.feature.chat.ui.chats.ChatsMoreBottomSheet
import com.salazar.cheers.feature.chat.ui.chats.ChatsSheetViewModel
import com.salazar.cheers.feature.chat.ui.chats.MessagesRoute
import com.salazar.cheers.feature.chat.ui.chats.NewChatRoute
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatRoute
import com.salazar.cheers.feature.chat.ui.screens.room.RoomRoute
import com.salazar.cheers.feature.create_note.createNoteScreen
import com.salazar.cheers.feature.create_note.navigateToCreateNote
import com.salazar.cheers.feature.create_post.createPostScreen
import com.salazar.cheers.feature.create_post.navigateToCreatePost
import com.salazar.cheers.feature.edit_profile.navigation.editProfileScreen
import com.salazar.cheers.feature.edit_profile.navigation.navigateToEditProfile
import com.salazar.cheers.feature.home.navigation.homeNavigationRoute
import com.salazar.cheers.feature.home.navigation.homeScreen
import com.salazar.cheers.feature.map.navigation.mapScreen
import com.salazar.cheers.feature.map.screens.settings.MapSettingsRoute
import com.salazar.cheers.feature.notifications.navigation.navigateToNotifications
import com.salazar.cheers.feature.notifications.navigation.notificationsScreen
import com.salazar.cheers.feature.profile.OtherProfileStatsRoute
import com.salazar.cheers.feature.profile.ProfileMoreBottomSheet
import com.salazar.cheers.feature.profile.ProfileSheetUIAction
import com.salazar.cheers.feature.profile.ProfileStatsRoute
import com.salazar.cheers.feature.profile.ProfileStatsViewModel
import com.salazar.cheers.feature.profile.navigation.navigateToOtherProfile
import com.salazar.cheers.feature.profile.navigation.otherProfileScreen
import com.salazar.cheers.feature.profile.navigation.profileScreen
import com.salazar.cheers.feature.search.navigation.navigateToSearch
import com.salazar.cheers.feature.search.navigation.searchScreen
import com.salazar.cheers.feature.settings.navigation.navigateToSettings
import com.salazar.cheers.friendship.ui.manage_friendship.ManageFriendshipRoute
import com.salazar.cheers.friendship.ui.manage_friendship.RemoveFriendDialog
import com.salazar.cheers.map.ui.MapPostHistoryRoute
import com.salazar.cheers.notes.ui.note.NoteRoute
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.ui.compose.sheets.StorySheetUIAction
import com.salazar.cheers.ui.main.camera.CameraRoute
import com.salazar.cheers.ui.main.camera.ChatCameraRoute
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.friendrequests.FriendRequestsRoute
import com.salazar.cheers.ui.main.nfc.NfcRoute
import com.salazar.cheers.ui.main.party.EventMoreBottomSheet
import com.salazar.cheers.ui.main.party.EventMoreSheetViewModel
import com.salazar.cheers.ui.main.party.EventsRoute
import com.salazar.cheers.ui.main.party.create.CreatePartyRoute
import com.salazar.cheers.ui.main.party.detail.EventDetailRoute
import com.salazar.cheers.ui.main.party.edit.EditEventRoute
import com.salazar.cheers.ui.main.party.guestlist.GuestListRoute
import com.salazar.cheers.ui.main.share.ShareRoute
import com.salazar.cheers.ui.main.stats.DrinkingStatsRoute
import com.salazar.cheers.ui.main.story.StoryRoute
import com.salazar.cheers.ui.main.story.feed.StoryFeedRoute
import com.salazar.cheers.ui.main.story.stats.StoryStatsRoute
import com.salazar.cheers.ui.main.ticketing.TicketingRoute
import com.salazar.cheers.ui.main.tickets.TicketsRoute
import com.salazar.cheers.ui.main.tickets.details.TicketDetailsRoute
import com.salazar.cheers.ui.sheets.DeletePostDialog
import com.salazar.cheers.ui.sheets.DeleteStoryDialog
import com.salazar.cheers.ui.sheets.SendGiftRoute
import com.salazar.cheers.ui.sheets.post_more.PostMoreRoute
import com.salazar.common.util.copyToClipboard


fun NavGraphBuilder.mainNavGraph(
    appState: CheersAppState,
) {
    val navController = appState.navController

    navigation(
        route = CheersDestinations.MAIN_ROUTE,
        startDestination = homeNavigationRoute,
    ) {
        bottomSheet(
            route = MainDestinations.MAP_SETTINGS_ROUTE,
        ) {
            MapSettingsRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = MainDestinations.FRIEND_REQUESTS,
        ) {
            FriendRequestsRoute(
                navActions = appState.navActions,
            )
        }

        bottomSheet(
            route = "${MainDestinations.SHARE_ROUTE}/{partyId}",
        ) {
            ShareRoute(
                appState = appState,
                navActions = appState.navActions,
            )
        }

        composable(
            route = MainDestinations.TICKETS_ROUTE,
        ) {
            TicketsRoute(
                navActions = appState.navActions
            )
        }

        composable(
            route = "${MainDestinations.TICKET_DETAILS_ROUTE}/{ticketId}",
        ) {
            TicketDetailsRoute(
                navActions = appState.navActions
            )
        }

        composable(
            route = MainDestinations.NFC_ROUTE,
        ) {
            NfcRoute(
                navActions = appState.navActions
            )
        }

        notificationsScreen(
            navigateBack = {
                navController.popBackStack()
            },
            navigateToPostDetail = {},
            navigateToFriendRequests = {},
            navigateToComments = {},
            navigateToOtherProfile = {
                navController.navigateToOtherProfile(it)
            },
        )

        composable(
            route = "${MainDestinations.CHAT_ROUTE}?channelId={channelId}&userId={userID}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/chat/{channelId}" }),
        ) {
            ChatRoute(
                navActions = appState.navActions,
                showSnackBar = appState::showSnackBar,
            )
        }

        bottomSheet(
            route = "${MainDestinations.SEND_GIFT_SHEET}/{receiverId}",
        ) {
            SendGiftRoute(
                navActions = appState.navActions,
                bottomSheetNavigator = appState.bottomSheetNavigator,
            )
        }

        composable(
            route = "${MainDestinations.ROOM_DETAILS}/{roomId}",
        ) {
            RoomRoute(
                navActions = appState.navActions,
                showSnackBar = appState::showSnackBar,
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

        dialog(
            route = "${MainDestinations.ADD_EVENT_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            CreatePartyRoute(
                navActions = appState.navActions,
            )
        }

        createNoteScreen(
            navigateBack = navController::popBackStack,
        )

        createPostScreen(
            navigateBack = navController::popBackStack,
            navigateToCamera = {
            },
        )

        composable(
            route = MainDestinations.EVENTS_ROUTE,
        ) {
            EventsRoute(
                navActions = appState.navActions,
            )
        }

        dialog(
            route = "${MainDestinations.DIALOG_DELETE_STORY}/{storyID}",
        ) {
            DeleteStoryDialog(
                appState = appState,
                navActions = appState.navActions
            )
        }

        dialog(
            route = "${MainDestinations.DIALOG_DELETE_POST}/{postID}",
        ) {
            DeletePostDialog(
                navActions = appState.navActions
            )
        }

        homeScreen(
            onActivityClick = navController::navigateToNotifications,
            onPostClick = { },
            navigateToSearch = navController::navigateToSearch,
            navigateToCreatePost = navController::navigateToCreatePost,
            navigateToCreateNote = navController::navigateToCreateNote,
            navigateToNote = appState.navActions.navigateToNote,
        )
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                com.salazar.cheers.core.share.ui.RequestPermission(permission = Manifest.permission.POST_NOTIFICATIONS)
//            }

        mapScreen(
            navigateBack = {
                navController.popBackStack()
            },
            navigateToMapSettings = {
            },
            navigateToCreatePost = {
            }
        )

        composable(MainDestinations.MAP_POST_HISTORY_ROUTE) {
            MapPostHistoryRoute(
                navActions = appState.navActions
            )
        }

        bottomSheet("${MainDestinations.CHAT_CAMERA_ROUTE}/{roomId}") {
            ChatCameraRoute(
                navActions = appState.navActions
            )
        }

        composable(MainDestinations.CAMERA_ROUTE) {
            CameraRoute(
                navActions = appState.navActions
            )
        }

        composable("${MainDestinations.TICKETING_ROUTE}/{eventId}") {
            TicketingRoute(
                navActions = appState.navActions,
            )
        }

        searchScreen(
            navigateToOtherProfile = navController::navigateToOtherProfile,
        )

        composable(
            route = "${MainDestinations.POST_COMMENTS}/{postId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/comments/{postId}" }),
        ) {
            CommentsRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.COMMENT_REPLIES}/{commentId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/comments/{commentId}/replies" }),
        ) {
            RepliesRoute(
                navActions = appState.navActions,
            )
        }

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
                navActions = appState.navActions
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
            navigateToOtherProfileStats = {
            },
            navigateToManageFriendship = {},
            navigateToPostDetail = {},
            navigateToComments = {},
        )

        composable(
            route = "${MainDestinations.GUEST_LIST_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/event/{eventId}" })
        ) {
            GuestListRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.EVENT_DETAIL_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/event/{eventId}" })
        ) {
            EventDetailRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.EDIT_EVENT_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/event/edit/{eventId}" })
        ) {
            EditEventRoute(
                navActions = appState.navActions,
            )
        }

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

        composable(
            route = "${MainDestinations.OTHER_PROFILE_STATS_ROUTE}/{username}/{verified}",
            arguments = listOf(
                navArgument("username") { nullable = false },
                navArgument("verified") { defaultValue = false }
            ),
        ) {
            OtherProfileStatsRoute(
                navActions = appState.navActions,
            )
        }

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

        composable(
            route = MainDestinations.MESSAGES_ROUTE,
        ) {
            MessagesRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = MainDestinations.NEW_CHAT_ROUTE,
        ) {
            NewChatRoute(
                navActions = appState.navActions,
            )
        }

        editProfileScreen(
            navigateBack = navController::popBackStack,
        )

        profileScreen(
            navigateToEditProfile = navController::navigateToEditProfile,
            navigateToProfileMore = {
                navController.navigate("${MainDestinations.PROFILE_MORE_SHEET}/$it")
            }
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
            route = "${MainDestinations.NOTE_SHEET}/{userID}",
            arguments = listOf(
                navArgument("userID") { nullable = false },
            )
        ) {
            NoteRoute(
                navigateBack = navController::popBackStack,
                navigateToCreateNote = navController::navigateToCreateNote,
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

            EventMoreBottomSheet(
                modifier = Modifier.navigationBarsPadding(),
                isAuthor = false,
                onDetails = { appState.navActions.navigateToEventDetail(eventId) },
                onDelete = { },
                onReport = { /*TODO*/ },
                onShare = {
                    FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
                        .addOnSuccessListener { shortLink ->
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shortLink.shortLink.toString())
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    appState.navActions.navigateBack()
                },
                onLinkClick = {
                    FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
                        .addOnSuccessListener { shortLink ->
                            context.copyToClipboard(shortLink.shortLink.toString())
                        }
                    appState.navActions.navigateBack()
                },
                onHide = {
                    viewModel.onHide()
                    appState.navActions.navigateBack()
                },
            )
        }
    }

    bottomSheet(route = "${MainDestinations.PROFILE_MORE_SHEET}/{username}") {
        val context = LocalContext.current
        val username = it.arguments?.getString("username")!!

        ProfileMoreBottomSheet(
            onProfileSheetUIAction = { action ->
                when (action) {
                    is ProfileSheetUIAction.OnNfcClick -> appState.navActions.navigateToNfc()
                    is ProfileSheetUIAction.OnSettingsClick -> navController.navigateToSettings()
                    is ProfileSheetUIAction.OnCopyProfileClick -> {
                        FirebaseDynamicLinksUtil.createShortLink("u/$username")
                            .addOnSuccessListener { shortLink ->
                                context.copyToClipboard(shortLink.shortLink.toString())
                            }
                        appState.navActions.navigateBack()
                    }
                    is ProfileSheetUIAction.OnAddSnapchatFriends -> context.shareToSnapchat(username)
                    is ProfileSheetUIAction.OnPostHistoryClick -> appState.navActions.navigateToPostHistory()
                }
            },
        )
    }

    bottomSheet(
        route = "${MainDestinations.MESSAGES_MORE_SHEET}/{channelId}",
    ) {
        val chatsSheetViewModel = hiltViewModel<ChatsSheetViewModel>()

        val uiState by chatsSheetViewModel.uiState.collectAsStateWithLifecycle()
        val room = uiState.room

        val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }

        if (room != null)
            ChatsMoreBottomSheet(
                modifier = Modifier.navigationBarsPadding(),
                name = room.name,
                isAdmin = room.admins.contains(uid),
                roomType = room.type,
                onDeleteClick = {
                    chatsSheetViewModel.deleteChannel {
                        appState.navActions.navigateBack()
                    }
                },
                onLeaveClick = {
                    chatsSheetViewModel.leaveChannel {
                        appState.navActions.navigateBack()
                    }
                },
                onDeleteChats = {
                    chatsSheetViewModel.deleteChats {
                        appState.navActions.navigateBack()
                    }
                }
            )
        else
            LoadingScreen()
    }
}