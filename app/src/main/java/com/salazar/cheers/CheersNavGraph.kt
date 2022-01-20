package com.salazar.cheers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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

@Composable
fun CheersNavGraph(
    user: User,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    navActions: CheersNavigationActions,
    startDestination: String = CheersDestinations.HOME_ROUTE
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        bottomSheet(
            route = "${CheersDestinations.CHAT_ROUTE}/{channelId}/{username}/{name}/{profilePictureUrl}",
        ) {
            val channelId = it.arguments?.getString("channelId")!!
            val username = it.arguments?.getString("username")!!
            val name = it.arguments?.getString("name")!!
            val profilePictureUrl2 = it.arguments?.getString("profilePictureUrl")!!

            val chatViewModel = chatViewModel(channelId = channelId)
            ChatRoute(
                chatViewModel = chatViewModel,
                navActions = navActions,
                username = username,
                name = name,
                profilePictureUrl = profilePictureUrl2,
            )
        }

        bottomSheet(route = "${CheersDestinations.POST_MORE_SHEET}/{postId}/{isAuthor}") {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            val postId = it.arguments?.getString("postId")!!
            val isAuthor = it.arguments?.getBoolean("isAuthor")!!
            PostMoreBottomSheet(
                isAuthor = isAuthor,
                onDelete = { homeViewModel.deletePost(postId) },
                onUnfollow = {},
                onReport = {},
                onShare = {},
            )
        }

        bottomSheet(route = CheersDestinations.PROFILE_MORE_SHEET) {
            ProfileMoreBottomSheet(
                onSettingsClick = { navActions.navigateToSettings() },
                onCopyProfileUrlClick = {}
            )
        }

        dialog(
            route = "${CheersDestinations.ADD_POST_SHEET}?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { nullable = true })
        ) {
            val viewModel = hiltViewModel<AddPostViewModel>()
            AddPostRoute(
                addPostViewModel = viewModel,
                navActions = navActions,
                profilePictureUrl = user.profilePictureUrl,
            )
            val photoUri = it.arguments?.getString("photoUri")
        }

        composable(CheersDestinations.HOME_ROUTE) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeRoute(
                homeViewModel = homeViewModel,
                navActions = navActions,
            )
        }

        composable(CheersDestinations.MAP_ROUTE) {
            val mapViewModel = hiltViewModel<MapViewModel>()
            MapRoute(
                mapViewModel = mapViewModel,
            )
        }

        composable(CheersDestinations.CAMERA_ROUTE) {
            val cameraViewModel = hiltViewModel<CameraViewModel>()
            CameraRoute(
                cameraViewModel = cameraViewModel,
                navActions = navActions
            )
        }

        composable(CheersDestinations.SEARCH_ROUTE) {
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchRoute(
                searchViewModel = searchViewModel,
                navActions = navActions,
            )
        }

        dialog(CheersDestinations.SETTINGS_ROUTE) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(
                settingsViewModel = settingsViewModel,
                navActions = navActions,
            )
        }

        composable(
            route = "${CheersDestinations.OTHER_PROFILE_ROUTE}/{username}",
        ) {

            val username = it.arguments?.getString("username")!!
            val otherProfileViewModel = otherProfileViewModel(username = username)
            OtherProfileRoute(
                otherProfileViewModel = otherProfileViewModel,
                navActions = navActions,
            )
        }

        composable(
            route = "${CheersDestinations.POST_DETAIL_ROUTE}/{postId}",
        ) {
            val postId = it.arguments?.getString("postId")!!
            val postDetailViewModel = postDetailViewModel(postId = postId)
            PostDetailRoute(
                postDetailViewModel = postDetailViewModel,
                navActions = navActions,
            )
        }

        composable(CheersDestinations.PROFILE_STATS_ROUTE) {
            val profileStatsViewModel = hiltViewModel<ProfileStatsViewModel>()
            ProfileStatsRoute(
                profileStatsViewModel = profileStatsViewModel,
                navActions = navActions,
            )
        }

        composable(CheersDestinations.MESSAGES_ROUTE) {
            val messagesViewModel = hiltViewModel<MessagesViewModel>()
            MessagesRoute(
                messagesViewModel = messagesViewModel,
                navActions = navActions,
                username = user.username,
            )
        }

        dialog(CheersDestinations.EDIT_PROFILE_ROUTE) {
            val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
            EditProfileRoute(
                editProfileViewModel = editProfileViewModel,
                navActions = navActions,
            )
        }

        composable(CheersDestinations.PROFILE_ROUTE) {
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