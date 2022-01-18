package com.salazar.cheers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.salazar.cheers.ui.add.AddPostRoute
import com.salazar.cheers.ui.add.AddPostViewModel
import com.salazar.cheers.ui.camera.CameraRoute
import com.salazar.cheers.ui.camera.CameraViewModel
import com.salazar.cheers.ui.chats.MessagesRoute
import com.salazar.cheers.ui.chats.MessagesViewModel
import com.salazar.cheers.ui.detail.PostDetailRoute
import com.salazar.cheers.ui.detail.postDetailViewModel
import com.salazar.cheers.ui.home.HomeRoute
import com.salazar.cheers.ui.home.HomeViewModel
import com.salazar.cheers.ui.map.MapRoute
import com.salazar.cheers.ui.map.MapViewModel
import com.salazar.cheers.ui.otherprofile.OtherProfileRoute
import com.salazar.cheers.ui.otherprofile.otherProfileViewModel
import com.salazar.cheers.ui.profile.ProfileRoute
import com.salazar.cheers.ui.profile.ProfileStatsRoute
import com.salazar.cheers.ui.profile.ProfileStatsViewModel
import com.salazar.cheers.ui.profile.ProfileViewModel
import com.salazar.cheers.ui.search.SearchRoute
import com.salazar.cheers.ui.search.SearchViewModel
import com.salazar.cheers.ui.settings.SettingsRoute
import com.salazar.cheers.ui.settings.SettingsViewModel

@Composable
fun CheersNavGraph(
    profilePictureUrl: String,
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
        composable(route = CheersDestinations.ADD_POST_SHEET) {
            val viewModel = hiltViewModel<AddPostViewModel>()
            AddPostRoute(
                addPostViewModel = viewModel,
                navActions = navActions,
                profilePictureUrl = profilePictureUrl,
            )
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
        composable(CheersDestinations.SETTINGS_ROUTE) {
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