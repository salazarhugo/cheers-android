package com.salazar.cheers.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.salazar.cheers.CheersUiState
import com.salazar.cheers.compose.CheersBottomBar
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.theme.GreySheet


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheersNavGraph(
    uiState: CheersUiState,
    showInterstitialAd: () -> Unit,
    appState: CheersAppState,
) {
    val startDestination = CheersDestinations.AUTH_ROUTE

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE
    val navActions = appState.navActions

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank())
            appState.snackBarHostState.showSnackbar(uiState.errorMessage)
    }

    val hide =
        navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.AUTH_ROUTE } == true
                || navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.SETTING_ROUTE } == true
                || currentRoute.contains(MainDestinations.STORY_ROUTE)
                || currentRoute.contains(MainDestinations.CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.ROOM_DETAILS)
                || currentRoute.contains(MainDestinations.POST_COMMENTS)
                || currentRoute.contains(MainDestinations.TICKETING_ROUTE)
                || currentRoute.contains(MainDestinations.ADD_POST_SHEET)
                || currentRoute.contains(MainDestinations.EDIT_PROFILE_ROUTE)
                || currentRoute.contains(MainDestinations.CAMERA_ROUTE)

    ModalBottomSheetLayout(
        bottomSheetNavigator = appState.bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (isSystemInDarkTheme()) GreySheet else MaterialTheme.colorScheme.background,
        sheetElevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(appState.snackBarHostState) },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                if (!hide)
                    FloatingActionButton(
                        onClick = {
                            navActions.navigateToAddEvent()
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
                ) {
                    CheersBottomBar(
                        unreadChatCount = uiState.unreadChatCount,
                        profilePictureUrl = "",
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            appState.navController.navigate(route)
                        },
                    )
                }
            },
        ) { innerPadding ->
            AnimatedNavHost(
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                route = CheersDestinations.ROOT_ROUTE,
                navController = appState.navController,
                startDestination = startDestination,
            ) {
                settingNavGraph(
                    navActions = navActions,
                )
                authNavGraph(navActions = navActions)
                mainNavGraph(
                    appState = appState,
                    showInterstitialAd = showInterstitialAd,
                )
            }
        }
    }
}