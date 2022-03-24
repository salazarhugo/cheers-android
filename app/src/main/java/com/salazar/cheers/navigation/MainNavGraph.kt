package com.salazar.cheers.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.salazar.cheers.components.PostMoreBottomSheet
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.add.AddPostRoute
import com.salazar.cheers.ui.main.add.AddPostViewModel
import com.salazar.cheers.ui.main.camera.CameraRoute
import com.salazar.cheers.ui.main.camera.CameraViewModel
import com.salazar.cheers.ui.main.chat.ChatRoute
import com.salazar.cheers.ui.main.chat.chatViewModel
import com.salazar.cheers.ui.main.chats.ChatsMoreBottomSheet
import com.salazar.cheers.ui.main.chats.MessagesRoute
import com.salazar.cheers.ui.main.chats.MessagesViewModel
import com.salazar.cheers.ui.main.comment.CommentsRoute
import com.salazar.cheers.ui.main.comment.commentsViewModel
import com.salazar.cheers.ui.main.detail.PostDetailRoute
import com.salazar.cheers.ui.main.detail.postDetailViewModel
import com.salazar.cheers.ui.main.editprofile.EditProfileRoute
import com.salazar.cheers.ui.main.editprofile.EditProfileViewModel
import com.salazar.cheers.ui.main.event.detail.EventDetailRoute
import com.salazar.cheers.ui.main.event.detail.eventDetailViewModel
import com.salazar.cheers.ui.main.home.HomeRoute
import com.salazar.cheers.ui.main.home.HomeViewModel
import com.salazar.cheers.ui.main.map.MapRoute
import com.salazar.cheers.ui.main.map.MapViewModel
import com.salazar.cheers.ui.main.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.main.otherprofile.otherProfileViewModel
import com.salazar.cheers.ui.main.profile.*
import com.salazar.cheers.ui.main.search.SearchRoute
import com.salazar.cheers.ui.main.search.SearchViewModel
import com.salazar.cheers.ui.main.stats.DrinkingStatsRoute
import com.salazar.cheers.ui.main.stats.drinkingStatsViewModel
import com.salazar.cheers.ui.main.story.StoryRoute
import com.salazar.cheers.ui.main.story.StoryViewModel
import com.salazar.cheers.ui.sheets.SendGiftRoute
import com.salazar.cheers.ui.sheets.sendGiftViewModel
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.util.Utils.copyToClipboard

fun NavGraphBuilder.mainNavGraph(
    user: User,
    navActions: CheersNavigationActions,
    presentPaymentSheet: (String) -> Unit,
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
            route = "${MainDestinations.CHAT_ROUTE}/{channelId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/chat/{channelId}" })
        ) {
            val channelId = it.arguments?.getString("channelId")!!
            val chatViewModel = chatViewModel(channelId = channelId)

            ChatRoute(
                chatViewModel = chatViewModel,
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
                onShare = {},
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
            route = "${MainDestinations.MESSAGES_MORE_SHEET}/{name}",
        ) {
            val name = it.arguments?.getString("name")!!
            ChatsMoreBottomSheet(
                name = name,
                onSettingsClick = { },
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
                onSettingsClick = { navActions.navigateToSettings() },
                onCopyProfileUrlClick = {
                    FirebaseDynamicLinksUtil.createShortLink(user.id)
                        .addOnSuccessListener { shortLink ->
                            context.copyToClipboard(shortLink.shortLink.toString())
                        }
                    navActions.navigateBack()
                }
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
            if (photoUri != null)
                viewModel.addPhoto(Uri.parse(photoUri))
        }

        composable(
            route = MainDestinations.HOME_ROUTE,
//            exitTransition = {},
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
            val otherProfileViewModel = otherProfileViewModel(username = username)

            OtherProfileRoute(
                otherProfileViewModel = otherProfileViewModel,
                navActions = navActions,
            )
        }

        composable(
            route = "${MainDestinations.EVENT_DETAIL_ROUTE}/{eventId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/e/{eventId}" })
        ) {
            val eventId = it.arguments?.getString("eventId")!!
            val eventDetailViewModel = eventDetailViewModel(eventId = eventId)
            EventDetailRoute(
                eventDetailViewModel = eventDetailViewModel,
                navActions = navActions,
            )
        }

        composable(
            route = "${MainDestinations.POST_DETAIL_ROUTE}/{postId}",
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/p/{postId}" })
        ) {
            val postId = it.arguments?.getString("postId")!!
            val postDetailViewModel = postDetailViewModel(postId = postId)
            PostDetailRoute(
                postDetailViewModel = postDetailViewModel,
                navActions = navActions,
            )
        }

        composable(MainDestinations.PROFILE_STATS_ROUTE) {
            val profileStatsViewModel = hiltViewModel<ProfileStatsViewModel>()
            ProfileStatsRoute(
                profileStatsViewModel = profileStatsViewModel,
                navActions = navActions,
                username = user.username,
                verified = user.verified,
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

//        composable(CheersDestinations.MESSAGES_ROUTE) {
//            val mapViewModel = hiltViewModel<MapViewModel>()
//            MapRoute(
//                mapViewModel = mapViewModel,
//            )
//        }
    }
}