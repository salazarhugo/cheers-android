package com.salazar.cheers.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.bottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.post.PostMoreBottomSheet
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.activity.ActivityRoute
import com.salazar.cheers.ui.main.add.AddPostRoute
import com.salazar.cheers.ui.main.add.AddPostViewModel
import com.salazar.cheers.ui.main.camera.CameraRoute
import com.salazar.cheers.ui.main.camera.CameraViewModel
import com.salazar.cheers.ui.main.camera.ChatCameraRoute
import com.salazar.cheers.ui.main.chat.ChatRoute
import com.salazar.cheers.ui.main.chats.*
import com.salazar.cheers.ui.main.comment.CommentsRoute
import com.salazar.cheers.ui.main.comment.commentsViewModel
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.detail.PostDetailViewModel
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
import com.salazar.cheers.ui.main.map.MapRoute
import com.salazar.cheers.ui.main.map.MapViewModel
import com.salazar.cheers.ui.main.nfc.NfcRoute
import com.salazar.cheers.ui.main.nfc.NfcViewModel
import com.salazar.cheers.ui.main.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileStatsRoute
import com.salazar.cheers.ui.main.otherprofile.OtherProfileViewModel
import com.salazar.cheers.ui.main.profile.*
import com.salazar.cheers.ui.main.room.RoomRoute
import com.salazar.cheers.ui.main.search.SearchRoute
import com.salazar.cheers.ui.main.search.SearchViewModel
import com.salazar.cheers.ui.main.stats.DrinkingStatsRoute
import com.salazar.cheers.ui.main.stats.drinkingStatsViewModel
import com.salazar.cheers.ui.main.story.StoryRoute
import com.salazar.cheers.ui.main.story.StoryViewModel
import com.salazar.cheers.ui.main.story.stats.StoryStatsRoute
import com.salazar.cheers.ui.main.story.stats.storyStatsViewModel
import com.salazar.cheers.ui.sheets.SendGiftRoute
import com.salazar.cheers.ui.sheets.sendGiftViewModel
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.util.Utils.copyToClipboard

fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    user: User,
    navActions: CheersNavigationActions,
    showInterstitialAd: () -> Unit,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val uri = "https://cheers-a275e.web.app"

    navigation(
        route = CheersDestinations.MAIN_ROUTE,
        startDestination = MainDestinations.HOME_ROUTE,
    ) {

//        settingNavGraph(
//            navActions = navActions,
//            presentPaymentSheet = presentPaymentSheet
//        )

        composable(
            route = MainDestinations.NFC_ROUTE,
        ) {
            val nfcViewModel = hiltViewModel<NfcViewModel>()

            NfcRoute(
                nfcViewModel = nfcViewModel,
                navActions = navActions
            )
        }

        composable(
            route = MainDestinations.ACTIVITY_ROUTE,
        ) {
            ActivityRoute(navActions = navActions)
        }

        composable(
            route = "${MainDestinations.CHAT_ROUTE}/{channelId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/chat/{channelId}" })
        ) {
            ChatRoute(
                navActions = navActions,
            )
        }

        bottomSheet(
            route = "${MainDestinations.POST_MORE_SHEET}/{postId}/{authorId}",
            arguments = listOf(
                navArgument("authorId") { nullable = false },
                navArgument("postId") { nullable = false },
            )
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val postId = it.arguments?.getString("postId")!!
            val authorId = it.arguments?.getString("authorId")!!
            val isAuthor = authorId == FirebaseAuth.getInstance().currentUser?.uid!!
            val context = LocalContext.current

            PostMoreBottomSheet(
                isAuthor = isAuthor,
                onDetails = { navActions.navigateToPostDetail(postId) },
                onDelete = { homeViewModel.deletePost(postId); navActions.navigateBack() },
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
                    navActions.navigateBack()
                },
                onBlock = {
                    homeViewModel.blockUser(authorId)
                    navActions.navigateBack()
                },
                onLinkClick = {
                    FirebaseDynamicLinksUtil.createShortLink("p/$postId")
                        .addOnSuccessListener { shortLink ->
                            context.copyToClipboard(shortLink.shortLink.toString())
                        }
                    navActions.navigateBack()
                }
            )
        }

        bottomSheet(
            route = "${MainDestinations.SEND_GIFT_SHEET}/{receiverId}",
        ) {
            val receiverId = it.arguments?.getString("receiverId")!!
            val sendGiftViewModel = sendGiftViewModel(receiverId = receiverId)

            SendGiftRoute(
                sendGiftViewModel = sendGiftViewModel,
                navActions = navActions,
                bottomSheetNavigator = bottomSheetNavigator,
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
                        navActions.navigateBack()
                    },
                    onLeaveClick = {
                        chatsSheetViewModel.leaveChannel()
                        navActions.navigateBack()
                    },
                    onDeleteChats = {
                        chatsSheetViewModel.deleteChats()
                        navActions.navigateBack()
                    }
                )
            else
                LoadingScreen()
        }

        composable(
            route = "${MainDestinations.ROOM_DETAILS}/{roomId}",
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
        ) {
            RoomRoute(
                navActions = navActions,
            )
        }

        composable(
            route = "${MainDestinations.STORY_ROUTE}?userId={userId}",
            arguments = listOf(navArgument("userId") { nullable = true }),
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
        ) {
            val storyViewModel = hiltViewModel<StoryViewModel>()
            val userId = it.arguments?.getString("userId")
//            if (userId != null)
//                storyViewModel.

            CheersTheme(darkTheme = true) {
                StoryRoute(
                    storyViewModel = storyViewModel,
                    navActions = navActions,
                    showInterstitialAd = showInterstitialAd,
                )
            }
        }

        bottomSheet(route = MainDestinations.PROFILE_MORE_SHEET) {
            val context = LocalContext.current
            ProfileMoreBottomSheet(
                onProfileSheetUIAction = { action ->
                    when (action) {
                        is ProfileSheetUIAction.OnNfcClick -> navActions.navigateToNfc()
                        is ProfileSheetUIAction.OnSettingsClick -> navActions.navigateToSettings()
                        is ProfileSheetUIAction.OnCopyProfileClick -> {
                            FirebaseDynamicLinksUtil.createShortLink(user.id)
                                .addOnSuccessListener { shortLink ->
                                    context.copyToClipboard(shortLink.shortLink.toString())
                                }
                            navActions.navigateBack()
                        }
                    }
                },
            )
        }

        dialog(
            route = "${MainDestinations.ADD_EVENT_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            AddEventRoute(
                navActions = navActions,
            )
        }

        dialog(
            route = "${MainDestinations.ADD_POST_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            val viewModel = hiltViewModel<AddPostViewModel>()
            AddPostRoute(
                addPostViewModel = viewModel,
                navActions = navActions,
                profilePictureUrl = user.profilePictureUrl,
            )
            val photoUri = it.arguments?.getString("photoUri")
            if (photoUri != null) {
                LaunchedEffect(photoUri) {
                    viewModel.addPhoto(Uri.parse(photoUri))
                }
            }
        }

        composable(
            route = MainDestinations.EVENTS_ROUTE,
        ) {
            EventsRoute(
                navActions = navActions,
            )
        }

        composable(
            route = MainDestinations.HOME_ROUTE,
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()

            HomeRoute(
                homeViewModel = homeViewModel,
                navActions = navActions,
            )
        }

        composable(MainDestinations.MAP_ROUTE) {
            val mapViewModel = hiltViewModel<MapViewModel>()
            MapRoute(
                mapViewModel = mapViewModel,
                navActions = navActions
            )
        }

        bottomSheet("${MainDestinations.CHAT_CAMERA_ROUTE}/{roomId}") {
            ChatCameraRoute(
                navActions = navActions
            )
        }

        dialog(MainDestinations.CAMERA_ROUTE) {
            val cameraViewModel = hiltViewModel<CameraViewModel>()
            CameraRoute(
                cameraViewModel = cameraViewModel,
                navActions = navActions
            )
        }

        composable(MainDestinations.SEARCH_ROUTE) {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchRoute(
                searchViewModel = searchViewModel,
                navActions = navActions,
            )
        }

        bottomSheet(
            route = "${MainDestinations.POST_COMMENTS}/{postId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/comments/{postId}" })
        ) {

            val postId = it.arguments?.getString("postId")!!
            val commentsViewModel = commentsViewModel(postId = postId)
            CommentsRoute(
                commentsViewModel = commentsViewModel,
                navActions = navActions,
                bottomSheetNavigator = bottomSheetNavigator,
                profilePictureUrl = user.profilePictureUrl,
            )
        }

        composable(
            route = "${MainDestinations.DRINKING_STATS}/{username}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/stats/{username}" })
        ) {

            val username = it.arguments?.getString("username")!!
            val drinkingStatsViewModel = drinkingStatsViewModel(username = username)

            DrinkingStatsRoute(
                navActions = navActions,
                drinkingStatsViewModel = drinkingStatsViewModel,
            )

        }

        composable(
            route = "${MainDestinations.OTHER_PROFILE_ROUTE}/{username}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/{username}" })
        ) {

            val username = it.arguments?.getString("username")!!
            val otherProfileViewModel = hiltViewModel<OtherProfileViewModel>()

            OtherProfileRoute(
                otherProfileViewModel = otherProfileViewModel,
                navActions = navActions,
                username = username,
            )
        }

        composable(
            route = "${MainDestinations.GUEST_LIST_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/event/{eventId}" })
        ) {
            GuestListRoute(
                navActions = navActions,
            )
        }

        composable(
            route = "${MainDestinations.EVENT_DETAIL_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/event/{eventId}" })
        ) {

            EventDetailRoute(
                navActions = navActions,
            )
        }

        bottomSheet("${MainDestinations.EVENT_MORE_SHEET}/{eventId}") {
            val eventId = it.arguments?.getString("eventId")!!
            val context = LocalContext.current
            val viewModel = hiltViewModel<EventMoreSheetViewModel>()

            EventMoreBottomSheet(
                isAuthor = false,
                onDetails = { navActions.navigateToEventDetail(eventId) },
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
                    navActions.navigateBack()
                },
                onLinkClick = {
                    FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
                        .addOnSuccessListener { shortLink ->
                            context.copyToClipboard(shortLink.shortLink.toString())
                        }
                    navActions.navigateBack()
                },
                onHide = {
                    viewModel.onHide()
                    navActions.navigateBack()
                },
            )
        }
    }

    composable(
        route = "${MainDestinations.EDIT_EVENT_ROUTE}/{eventId}",
        deepLinks = listOf(navDeepLink { uriPattern = "$uri/event/edit/{eventId}" })
    ) {

        EditEventRoute(
            navActions = navActions,
        )
    }

    composable(
        route = "${MainDestinations.STORY_STATS_ROUTE}/{storyId}",
        deepLinks = listOf(navDeepLink { uriPattern = "$uri/p/{storyId}" })
    ) {
        val storyId = it.arguments?.getString("storyId")!!
        val storyStatsViewModel = storyStatsViewModel(storyId = storyId)

        StoryStatsRoute(
            storyStatsViewModel = storyStatsViewModel,
            navActions = navActions,
        )
    }

    composable(
        route = "${MainDestinations.POST_DETAIL_ROUTE}/{postId}",
        deepLinks = listOf(navDeepLink { uriPattern = "$uri/p/{postId}" })
    ) {

        PostDetailRoute(
            navActions = navActions,
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
            navActions = navActions,
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
            navActions = navActions,
            username = username,
            verified = verified,
        )
    }

    composable(
        MainDestinations.MESSAGES_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
    ) {
        val messagesViewModel = hiltViewModel<MessagesViewModel>()

        MessagesRoute(
            messagesViewModel = messagesViewModel,
            navActions = navActions,
        )
    }

    bottomSheet(MainDestinations.NEW_CHAT_ROUTE) {
        NewChatRoute(
            navActions = navActions,
        )
    }


    dialog(MainDestinations.EDIT_PROFILE_ROUTE) {
        val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
        EditProfileRoute(
            editProfileViewModel = editProfileViewModel,
            navActions = navActions,
        )
    }

    composable(
        MainDestinations.PROFILE_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = {
            if (targetState.destination.hierarchy.any { it.route == CheersDestinations.SETTING_ROUTE })
                slideOutHorizontally(targetOffsetX = { -1000 })
            else
                slideOutHorizontally(targetOffsetX = { 1000 })
        }

    ) {
        val profileViewModel = hiltViewModel<ProfileViewModel>()

        ProfileRoute(
            profileViewModel = profileViewModel,
            navActions = navActions,
            username = user.username,
        )
    }
}