package com.salazar.cheers.navigation

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.post.PostMoreBottomSheet
import com.salazar.cheers.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.compose.sheets.StorySheetUIAction
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.main.activity.ActivityRoute
import com.salazar.cheers.ui.main.add.AddPostRoute
import com.salazar.cheers.ui.main.camera.CameraRoute
import com.salazar.cheers.ui.main.camera.ChatCameraRoute
import com.salazar.cheers.ui.main.chat.ChatRoute
import com.salazar.cheers.ui.main.chats.ChatsMoreBottomSheet
import com.salazar.cheers.ui.main.chats.ChatsSheetViewModel
import com.salazar.cheers.ui.main.chats.MessagesRoute
import com.salazar.cheers.ui.main.chats.NewChatRoute
import com.salazar.cheers.ui.main.comment.CommentsRoute
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.editprofile.EditProfileRoute
import com.salazar.cheers.ui.main.editprofile.EditProfileViewModel
import com.salazar.cheers.ui.main.event.EventMoreBottomSheet
import com.salazar.cheers.ui.main.event.EventMoreSheetViewModel
import com.salazar.cheers.ui.main.event.EventsRoute
import com.salazar.cheers.ui.main.event.add.AddEventRoute
import com.salazar.cheers.ui.main.event.detail.EventDetailRoute
import com.salazar.cheers.ui.main.event.edit.EditEventRoute
import com.salazar.cheers.ui.main.event.guestlist.GuestListRoute
import com.salazar.cheers.ui.main.home.HomeRoute
import com.salazar.cheers.ui.main.home.HomeViewModel
import com.salazar.cheers.ui.main.map.MapPostHistoryRoute
import com.salazar.cheers.ui.main.map.MapRoute
import com.salazar.cheers.ui.main.nfc.NfcRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileStatsRoute
import com.salazar.cheers.ui.main.profile.*
import com.salazar.cheers.ui.main.room.RoomRoute
import com.salazar.cheers.ui.main.search.SearchRoute
import com.salazar.cheers.ui.main.stats.DrinkingStatsRoute
import com.salazar.cheers.ui.main.story.StoryRoute
import com.salazar.cheers.ui.main.story.feed.SetStoryStatusBars
import com.salazar.cheers.ui.main.story.feed.StoryFeedRoute
import com.salazar.cheers.ui.main.story.stats.StoryStatsRoute
import com.salazar.cheers.ui.main.ticketing.TicketingRoute
import com.salazar.cheers.ui.sheets.DeletePostDialog
import com.salazar.cheers.ui.sheets.DeleteStoryDialog
import com.salazar.cheers.ui.sheets.SendGiftRoute
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.Constants.URI
import com.salazar.cheers.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.util.Utils.copyToClipboard
import com.salazar.cheers.util.Utils.shareToSnapchat


fun NavGraphBuilder.mainNavGraph(
    appState: CheersAppState,
    showInterstitialAd: () -> Unit,
) {
    navigation(
        route = CheersDestinations.MAIN_ROUTE,
        startDestination = MainDestinations.HOME_ROUTE,
    ) {
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
            ActivityRoute(navActions = appState.navActions)
        }

        composable(
            route = "${MainDestinations.CHAT_ROUTE}?channelId={channelId}&userId={userID}",
            deepLinks = listOf(navDeepLink { uriPattern = "$URI/chat/{channelId}" })
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
                    showInterstitialAd = showInterstitialAd,
                    bottomSheetNavigator = appState.bottomSheetNavigator,
                )
            }
        }

        dialog(
            route = "${MainDestinations.ADD_EVENT_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            AddEventRoute(
                navActions = appState.navActions,
            )
        }

        composable(
            route = "${MainDestinations.ADD_POST_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            AddPostRoute(
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
            SetStoryStatusBars()
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
            enterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
        ) {
            MessagesRoute(
                navActions = appState.navActions,
            )
        }

        bottomSheet(
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
                    }
                }
            })
        }

        bottomSheet(
            route = "${MainDestinations.POST_MORE_SHEET}/{postID}",
            arguments = listOf(
                navArgument("postID") { nullable = false },
            )
        ) {
            val postId = it.arguments?.getString("postID")!!
            val isAuthor = true
            val context = LocalContext.current

            PostMoreBottomSheet(
                isAuthor = isAuthor,
                onDetails = { appState.navActions.navigateToPostDetail(postId) },
                onDelete = {
                    appState.navActions.navigateToDeletePostDialog(postId)
                           },
                onUnfollow = {}, //{ homeViewModel.unfollowUser(post.creator.username)},
                onReport = {},
                onShare = {
                    FirebaseDynamicLinksUtil.createShortLink("p/$postId")
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
                onBlock = {
                    appState.navActions.navigateBack()
                },
                onLinkClick = {
                    FirebaseDynamicLinksUtil.createShortLink("p/$postId")
                        .addOnSuccessListener { shortLink ->
                            context.copyToClipboard(shortLink.shortLink.toString())
                        }
                    appState.navActions.navigateBack()
                }
            )
        }

        bottomSheet("${MainDestinations.EVENT_MORE_SHEET}/{eventId}") {
            val eventId = it.arguments?.getString("eventId")!!
            val context = LocalContext.current
            val viewModel = hiltViewModel<EventMoreSheetViewModel>()

            EventMoreBottomSheet(
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

        val uiState by chatsSheetViewModel.uiState.collectAsState()
        val room = uiState.room

        if (room != null)
            ChatsMoreBottomSheet(
                name = room.name,
                ownerId = room.ownerId,
                roomType = room.type,
                onDeleteClick = {
                    chatsSheetViewModel.deleteChannel()
                    appState.navActions.navigateBack()
                },
                onLeaveClick = {
                    chatsSheetViewModel.leaveChannel()
                    appState.navActions.navigateBack()
                },
                onDeleteChats = {
                    chatsSheetViewModel.deleteChats()
                    appState.navActions.navigateBack()
                }
            )
        else
            LoadingScreen()
    }
}