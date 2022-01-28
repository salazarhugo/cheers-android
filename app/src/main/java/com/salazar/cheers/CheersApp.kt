package com.salazar.cheers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.internal.User
import com.salazar.cheers.navigation.CheersNavGraph
import com.salazar.cheers.navigation.CheersNavigationActions
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

            val mainViewModel = hiltViewModel<MainViewModel>()

            CheersNavGraph(
                user = mainViewModel.user2.value ?: User(),
                navActions = navigationActions
            )
        }
    }
}
