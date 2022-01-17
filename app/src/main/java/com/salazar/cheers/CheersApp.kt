package com.salazar.cheers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.components.CheersNavigationBar
import com.salazar.cheers.ui.chats.MessagesViewModel
import com.salazar.cheers.ui.theme.CheersTheme

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
            val navigationActions = remember(navController) {
                CheersNavigationActions(navController)
            }

            val coroutineScope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: CheersDestinations.HOME_ROUTE

            val mainViewModel = hiltViewModel<MainViewModel>()

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                bottomBar = {
                    CheersNavigationBar(
                        profilePictureUrl = mainViewModel.user2.value?.profilePictureUrl?: "",
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToMap = navigationActions.navigateToMap,
                        navigateToSearch = navigationActions.navigateToSearch,
                        navigateToMessages = navigationActions.navigateToMessages,
                        navigateToProfile = navigationActions.navigateToProfile,
                    )
                },
            ) { innerPadding ->
                CheersNavGraph(
                    navController = navController,
                    navActions = navigationActions,
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
    }
}