package com.salazar.cheers.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.plusAssign
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.CheersNavigationBar
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.GreySheet

@Composable
fun CheersNavGraph(
    user: User,
    navController: NavHostController = rememberAnimatedNavController(),
    navActions: CheersNavigationActions,
) {
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser == null)
            CheersDestinations.AUTH_ROUTE
        else
            CheersDestinations.MAIN_ROUTE

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    val navActions = remember(navController) {
        CheersNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE
    val authDestinations = listOf(
        AuthDestinations.SIGN_IN_ROUTE,
        AuthDestinations.SIGN_UP_ROUTE,
        AuthDestinations.CHOOSE_USERNAME,
        AuthDestinations.PHONE_ROUTE
    )

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (isSystemInDarkTheme()) GreySheet else MaterialTheme.colorScheme.background,
        sheetElevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Scaffold(
            bottomBar = {
                if (!authDestinations.contains(currentRoute))
                    Column {
                        DividerM3()
                        CheersNavigationBar(
                            profilePictureUrl = user.profilePictureUrl,
                            currentRoute = currentRoute,
                            navigateToHome = navActions.navigateToHome,
                            navigateToMap = navActions.navigateToMap,
                            navigateToSearch = navActions.navigateToSearch,
                            navigateToCamera = navActions.navigateToCamera,
                            navigateToMessages = navActions.navigateToMessages,
                            navigateToProfile = navActions.navigateToProfile,
                        )
                    }
            },
        ) { innerPadding ->
            AnimatedNavHost(
                route = CheersDestinations.ROOT_ROUTE,
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                authNavGraph(navActions = navActions)
                mainNavGraph(user = user, navActions = navActions)
            }
        }
    }
}