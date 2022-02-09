package com.salazar.cheers.navigation

import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.bottomSheet
import com.salazar.cheers.components.PostMoreBottomSheet
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.add.AddPostRoute
import com.salazar.cheers.ui.add.AddPostViewModel
import com.salazar.cheers.ui.camera.CameraRoute
import com.salazar.cheers.ui.camera.CameraViewModel
import com.salazar.cheers.ui.chat.ChatRoute
import com.salazar.cheers.ui.chat.chatViewModel
import com.salazar.cheers.ui.chats.MessagesRoute
import com.salazar.cheers.ui.chats.MessagesViewModel
import com.salazar.cheers.ui.comment.CommentsRoute
import com.salazar.cheers.ui.comment.commentsViewModel
import com.salazar.cheers.ui.detail.PostDetailRoute
import com.salazar.cheers.ui.detail.postDetailViewModel
import com.salazar.cheers.ui.editprofile.EditProfileRoute
import com.salazar.cheers.ui.editprofile.EditProfileViewModel
import com.salazar.cheers.ui.home.HomeRoute
import com.salazar.cheers.ui.home.HomeViewModel
import com.salazar.cheers.ui.map.MapRoute
import com.salazar.cheers.ui.map.MapViewModel
import com.salazar.cheers.ui.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.otherprofile.otherProfileViewModel
import com.salazar.cheers.ui.profile.*
import com.salazar.cheers.ui.search.SearchRoute
import com.salazar.cheers.ui.search.SearchViewModel
import com.salazar.cheers.ui.settings.SettingsRoute
import com.salazar.cheers.ui.settings.SettingsViewModel

fun NavGraphBuilder.mainNavGraph(
    user: User,
    navActions: CheersNavigationActions,
) {
    val uri = "https://cheers-a275e.web.app"

    navigation(
        route = CheersDestinations.MAIN_ROUTE,
        startDestination = MainDestinations.HOME_ROUTE,
    ) {

        bottomSheet(
            route = "${MainDestinations.CHAT_ROUTE}/{channelId}/{username}/{verified}/{name}/{profilePictureUrl}",
        ) {
            val channelId = it.arguments?.getString("channelId")!!
            val username = it.arguments?.getString("username")!!
            val verified = it.arguments?.getBoolean("verified")!!
            val name = it.arguments?.getString("name")!!
            val profilePictureUrl2 = it.arguments?.getString("profilePictureUrl")!!

            val chatViewModel = chatViewModel(channelId = channelId)
            ChatRoute(
                chatViewModel = chatViewModel,
                navActions = navActions,
                username = username,
                verified = verified,
                name = name,
                profilePictureUrl = profilePictureUrl2,
            )
        }

        bottomSheet(
            route = "${MainDestinations.POST_MORE_SHEET}/{postId}/{isAuthor}",
            arguments = listOf(navArgument("isAuthor") { defaultValue = false })
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val postId = it.arguments?.getString("postId")!!
            val isAuthor = it.arguments?.getBoolean("isAuthor")!!
            PostMoreBottomSheet(
                isAuthor = isAuthor,
                onDetails = { navActions.navigateToPostDetail(postId) },
                onDelete = { homeViewModel.deletePost(postId); navActions.navigateBack() },
                onUnfollow = {}, //{ homeViewModel.unfollowUser(post.creator.username)},
                onReport = {},
                onShare = {},
            )
        }

        bottomSheet(route = MainDestinations.PROFILE_MORE_SHEET) {
            ProfileMoreBottomSheet(
                onSettingsClick = { navActions.navigateToSettings() },
                onCopyProfileUrlClick = {}
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
                viewModel.setPostImage(Uri.parse(photoUri))
        }

        composable(MainDestinations.HOME_ROUTE) {
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

        dialog(MainDestinations.SETTINGS_ROUTE) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(
                settingsViewModel = settingsViewModel,
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
            route = "${MainDestinations.POST_DETAIL_ROUTE}/{postId}",
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

        composable(MainDestinations.MESSAGES_ROUTE) {
            val messagesViewModel = hiltViewModel<MessagesViewModel>()
            MessagesRoute(
                messagesViewModel = messagesViewModel,
                navActions = navActions,
                username = user.username,
                verified = user.verified,
            )
        }

        dialog(MainDestinations.EDIT_PROFILE_ROUTE) {
            val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
            EditProfileRoute(
                editProfileViewModel = editProfileViewModel,
                navActions = navActions,
            )
        }

        composable(MainDestinations.PROFILE_ROUTE) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            ProfileRoute(
                profileViewModel = profileViewModel,
                navActions = navActions,
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