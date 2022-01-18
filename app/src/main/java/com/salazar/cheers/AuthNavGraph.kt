package com.salazar.cheers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.*
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
fun AuthNavGraph(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberAnimatedNavController(),
    navActions: CheersNavigationActions,
    startDestination: String = CheersDestinations.HOME_ROUTE
) {
    ModalBottomSheetLayout(bottomSheetNavigator) {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
        }
    }
}