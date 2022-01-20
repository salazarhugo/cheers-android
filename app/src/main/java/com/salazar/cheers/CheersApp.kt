package com.salazar.cheers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.components.CheersNavigationBar
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.chats.MessagesViewModel
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.GreySheet

@Composable
fun CheersApp() {
    CheersTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val darkIcons = !isSystemInDarkTheme()

            SideEffect {
                systemUiController.setSystemBarsColor(
                    if (darkIcons) Color.White else Color.Black,
                    darkIcons = darkIcons
                )
            }

            val navController = rememberAnimatedNavController()
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider += bottomSheetNavigator

            val navigationActions = remember(navController) {
                CheersNavigationActions(navController)
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: CheersDestinations.HOME_ROUTE

            val mainViewModel = hiltViewModel<MainViewModel>()

            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                sheetBackgroundColor = if (darkIcons) MaterialTheme.colorScheme.surface else GreySheet,
                sheetElevation = 0.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                Scaffold(
                    bottomBar = {
                        Column {
                            DividerM3()
                            CheersNavigationBar(
                                profilePictureUrl = mainViewModel.user2.value?.profilePictureUrl?: "",
                                currentRoute = currentRoute,
                                navigateToHome = navigationActions.navigateToHome,
                                navigateToMap = navigationActions.navigateToMap,
                                navigateToSearch = navigationActions.navigateToSearch,
                                navigateToCamera = navigationActions.navigateToCamera,
                                navigateToMessages = navigationActions.navigateToMessages,
                                navigateToProfile = navigationActions.navigateToProfile,
                            )
                        }
                    },
                ) { innerPadding ->
                    CheersNavGraph(
                        user = mainViewModel.user2.value ?: User(),
                        navController = navController,
                        navActions = navigationActions,
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}