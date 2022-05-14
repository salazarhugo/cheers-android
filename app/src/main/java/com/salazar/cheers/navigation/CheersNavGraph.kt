package com.salazar.cheers.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.salazar.cheers.components.CheersNavigationBar
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.GreySheet

@Composable
fun CheersNavGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    darkTheme: Boolean,
    showInterstitialAd: () -> Unit,
    user: User?,
) {
    val startDestination = CheersDestinations.AUTH_ROUTE
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    val navActions = remember(navController) {
        CheersNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (darkTheme) GreySheet else MaterialTheme.colorScheme.background,
        sheetElevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Scaffold(
            bottomBar = {
                val visible =
                    navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.MAIN_ROUTE } == true &&
                            !currentRoute.contains(MainDestinations.STORY_ROUTE) && !currentRoute.contains(
                        MainDestinations.CHAT_ROUTE
                    )
                if (visible)
                    Column {
                        DividerM3()
                        CheersNavigationBar(
                            profilePictureUrl = user?.profilePictureUrl ?: "",
                            currentRoute = currentRoute,
                            navigateToHome = navActions.navigateToHome,
                            navigateToMap = navActions.navigateToMap,
                            navigateToSearch = navActions.navigateToSearch,
                            navigateToCamera = navActions.navigateToCamera,
                            navigateToMessages = navActions.navigateToMessages,
                            navigateToProfile = navActions.navigateToProfile,
                        )
                    }
//                }
            },
        ) { innerPadding ->
            AnimatedNavHost(
                route = CheersDestinations.ROOT_ROUTE,
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                settingNavGraph(
                    navActions = navActions,
                )
                authNavGraph(navActions = navActions)
                mainNavGraph(
                    navController = navController,
                    user = user ?: User(),
                    navActions = navActions,
                    bottomSheetNavigator = bottomSheetNavigator,
                    showInterstitialAd = showInterstitialAd,
                )
            }
        }
    }
}