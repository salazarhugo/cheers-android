package com.salazar.cheers.navigation

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.bottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.auth.ui.components.delete_account.DeleteAccountDialog
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatRoute
import com.salazar.cheers.feature.chat.ui.screens.room.RoomRoute
import com.salazar.cheers.comment.ui.comment_more.CommentMoreRoute
import com.salazar.cheers.comment.ui.comments.CommentsRoute
import com.salazar.cheers.comment.ui.delete.DeleteCommentDialog
import com.salazar.cheers.comment.ui.replies.RepliesRoute
import com.salazar.cheers.core.data.util.Constants.URI
import com.salazar.cheers.core.data.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.core.data.util.Utils.copyToClipboard
import com.salazar.cheers.core.data.util.Utils.shareToSnapchat
import com.salazar.cheers.core.share.ui.CheersDestinations
import com.salazar.cheers.friendship.ui.manage_friendship.ManageFriendshipRoute
import com.salazar.cheers.friendship.ui.manage_friendship.RemoveFriendDialog
import com.salazar.cheers.map.screens.map.MapRoute
import com.salazar.cheers.map.screens.settings.MapSettingsRoute
import com.salazar.cheers.map.ui.MapPostHistoryRoute
import com.salazar.cheers.notes.ui.create_note.CreateNoteRoute
import com.salazar.cheers.notes.ui.note.NoteRoute
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.share.ui.MainDestinations
import com.salazar.cheers.ui.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.ui.compose.sheets.StorySheetUIAction
import com.salazar.cheers.core.share.ui.RequestPermission
import com.salazar.cheers.ui.main.activity.ActivityRoute
import com.salazar.cheers.ui.main.add.CreatePostRoute
import com.salazar.cheers.ui.main.camera.CameraRoute
import com.salazar.cheers.ui.main.camera.ChatCameraRoute
import com.salazar.cheers.feature.chat.ui.chats.ChatsMoreBottomSheet
import com.salazar.cheers.feature.chat.ui.chats.ChatsSheetViewModel
import com.salazar.cheers.feature.chat.ui.chats.MessagesRoute
import com.salazar.cheers.feature.chat.ui.chats.NewChatRoute
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.editprofile.EditProfileRoute
import com.salazar.cheers.ui.main.editprofile.EditProfileViewModel
import com.salazar.cheers.ui.main.friendrequests.FriendRequestsRoute
import com.salazar.cheers.ui.main.home.HomeRoute
import com.salazar.cheers.ui.main.home.HomeViewModel
import com.salazar.cheers.ui.main.nfc.NfcRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileStatsRoute
import com.salazar.cheers.ui.main.party.EventMoreBottomSheet
import com.salazar.cheers.ui.main.party.EventMoreSheetViewModel
import com.salazar.cheers.ui.main.party.EventsRoute
import com.salazar.cheers.ui.main.party.create.CreatePartyRoute
import com.salazar.cheers.ui.main.party.detail.EventDetailRoute
import com.salazar.cheers.ui.main.party.edit.EditEventRoute
import com.salazar.cheers.ui.main.party.guestlist.GuestListRoute
import com.salazar.cheers.ui.main.profile.*
import com.salazar.cheers.ui.main.search.SearchRoute
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
import com.salazar.cheers.core.ui.theme.CheersTheme


fun NavGraphBuilder.mainNavGraph(
    appState: CheersAppState,
) {
    navigation(
        route = CheersDestinations.MAIN_ROUTE,
        startDestination = MainDestinations.HOME_ROUTE,
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

        composable(
            route = MainDestinations.ACTIVITY_ROUTE,
        ) {
            ActivityRoute(
                appState = appState,
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.CHAT_ROUTE}?channelId={channelId}&userId={userID}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/chat/{channelId}" }),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 })
                              },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
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
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
        ) {
            RoomRoute(
                navActions = appState.navActions,
                showSnackBar = appState::showSnackBar,
            )
        }

        composable(
            route = "${MainDestinations.STORY_FEED_ROUTE}/{page}",
            arguments = listOf(navArgument("page") { type = NavType.IntType }),
            enterTransition = { scaleIn(animationSpec = tween(200)) },
            exitTransition = { scaleOut(animationSpec = tween(200)) },
        ) {
                StoryFeedRoute(
                    appState = appState,
                    navActions = appState.navActions,
                )
        }

        composable(
            route = "${MainDestinations.STORY_ROUTE}?username={username}",
            arguments = listOf(navArgument("username") { nullable = true }),
            enterTransition = { scaleIn(animationSpec = tween(200)) },
            exitTransition = { scaleOut(animationSpec = tween(200)) },
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

        composable(
            route = MainDestinations.CREATE_NOTE_ROUTE,
        ) {
            CreateNoteRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.CREATE_POST_ROUTE}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            CreatePostRoute(
                navActions = appState.navActions,
            )
        }

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

        composable(
            route = MainDestinations.HOME_ROUTE,
        ) { back ->
            val parentEntry = remember(back) {
                appState.navController.getBackStackEntry(CheersDestinations.MAIN_ROUTE)
            }
            val homeViewModel = hiltViewModel<HomeViewModel>(parentEntry)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                com.salazar.cheers.core.share.ui.RequestPermission(permission = Manifest.permission.POST_NOTIFICATIONS)
            }
            HomeRoute(
                appState = appState,
                navActions = appState.navActions,
                homeViewModel = homeViewModel,
            )
        }

        composable(MainDestinations.MAP_ROUTE) {
            MapRoute(
                navActions = appState.navActions
            )
        }

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

        composable(MainDestinations.SEARCH_ROUTE) {
            SearchRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.POST_COMMENTS}/{postId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/comments/{postId}" }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            CommentsRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.COMMENT_REPLIES}/{commentId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/comments/{commentId}/replies" }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
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

        composable(
            route = "${MainDestinations.OTHER_PROFILE_ROUTE}/{username}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/u/{username}" })
        ) {

            val username = it.arguments?.getString("username")!!

            OtherProfileRoute(
                navActions = appState.navActions,
                username = username,
            )
        }

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
            enterTransition = {
                val dir = if (this.initialState.destination.route?.contains(MainDestinations.CHAT_ROUTE) == true)
                    -1000
                else
                    1000
                slideInHorizontally(initialOffsetX = { dir })
                              },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
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

        composable(
            route = MainDestinations.EDIT_PROFILE_ROUTE,
        ) {
            val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
            EditProfileRoute(
                editProfileViewModel = editProfileViewModel,
                navActions = appState.navActions,
            )
        }

        composable(
            route = MainDestinations.PROFILE_ROUTE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = {
                if (targetState.destination.hierarchy.any { it.route == CheersDestinations.SETTING_ROUTE })
                    slideOutHorizontally(targetOffsetX = { -1000 })
                else
                    slideOutHorizontally(targetOffsetX = { 1000 })
            }
        ) {
            ProfileRoute(
                navActions = appState.navActions,
                showSnackBar = appState::showSnackBar,
                appState = appState,
            )
        }

        bottomSheet(
            route = "${MainDestinations.STORY_MORE_SHEET}/{storyID}",
        ) {
            val storyID = it.arguments?.getString("storyID")!!

            StoryMoreBottomSheet(onStorySheetUIAction = { action ->
                when(action) {
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
                navActions = appState.navActions,
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
                    is ProfileSheetUIAction.OnSettingsClick -> appState.navActions.navigateToSettings()
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
            com.salazar.cheers.core.share.ui.LoadingScreen()
    }
}