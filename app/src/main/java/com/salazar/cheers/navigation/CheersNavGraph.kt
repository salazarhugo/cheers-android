package com.salazar.cheers.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.salazar.cheers.CheersUiState
import com.salazar.cheers.components.CheersNavigationBar
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.GreySheet

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun CheersNavGraph(
    uiState: CheersUiState,
    darkTheme: Boolean,
    showInterstitialAd: () -> Unit,
    user: User?,
) {
    val startDestination = CheersDestinations.AUTH_ROUTE
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val snackBarHostState = remember { SnackbarHostState() }

    val navActions = remember(navController) {
        CheersNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank())
            snackBarHostState.showSnackbar(uiState.errorMessage)
    }

    val hide =
        navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.AUTH_ROUTE } == true
                || navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.SETTING_ROUTE } == true
                || currentRoute.contains(MainDestinations.STORY_ROUTE)
                || currentRoute.contains(MainDestinations.CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.ROOM_DETAILS)
                || currentRoute.contains(MainDestinations.POST_COMMENTS)
                || currentRoute.contains(MainDestinations.TICKETING_ROUTE)

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
            snackbarHost = { SnackbarHost(snackBarHostState) },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                if (!hide)
                    FloatingActionButton(
                        onClick = {
                            navActions.navigateToAddPostSheet()
                        },
                        modifier = Modifier.offset(y = (+58).dp),
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = !hide,
//                    exit = shrinkOut(animationSpec = tween(2000))
                ) {
                    CheersNavigationBar(
                        unreadChatCount = uiState.unreadChatCount,
                        profilePictureUrl = user?.profilePictureUrl ?: "",
                        currentRoute = currentRoute,
                        navigateToHome = navActions.navigateToHome,
                        navigateToMap = navActions.navigateToMap,
                        navigateToSearch = navActions.navigateToSearch,
                        navigateToCamera = navActions.navigateToEvents,
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
                settingNavGraph(
                    navActions = navActions,
                )
                authNavGraph(navActions = navActions)
                mainNavGraph(
                    navActions = navActions,
                    bottomSheetNavigator = bottomSheetNavigator,
                    showInterstitialAd = showInterstitialAd,
                )
            }
        }
    }
}