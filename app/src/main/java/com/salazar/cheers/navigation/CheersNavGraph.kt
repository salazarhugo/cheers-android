package com.salazar.cheers.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import com.salazar.cheers.core.share.ui.CheersDestinations
import com.salazar.cheers.core.share.ui.GreySheet
import com.salazar.cheers.core.share.ui.MainDestinations
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.CheersBottomBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheersNavGraph(
    uiState: CheersUiState,
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
                || currentRoute.contains(MainDestinations.MESSAGES_ROUTE)
                || currentRoute.contains(MainDestinations.CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.NEW_CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.ROOM_DETAILS)
                || currentRoute.contains(MainDestinations.POST_COMMENTS)
                || currentRoute.contains(MainDestinations.COMMENT_REPLIES)
                || currentRoute.contains(MainDestinations.COMMENT_MORE_SHEET)
                || currentRoute.contains(MainDestinations.COMMENT_DELETE)
                || currentRoute.contains(MainDestinations.TICKETING_ROUTE)
                || currentRoute.contains(MainDestinations.CREATE_POST_ROUTE)
                || currentRoute.contains(MainDestinations.CREATE_NOTE_ROUTE)
                || currentRoute.contains(MainDestinations.EDIT_PROFILE_ROUTE)
                || currentRoute.contains(MainDestinations.CAMERA_ROUTE)

    ModalBottomSheetLayout(
        bottomSheetNavigator = appState.bottomSheetNavigator,
        sheetShape = MaterialTheme.shapes.extraLarge,
        sheetBackgroundColor = if (isSystemInDarkTheme()) GreySheet else MaterialTheme.colorScheme.background,
        sheetElevation = 0.dp,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(appState.snackBarHostState) },
            bottomBar = {
                AnimatedVisibility(
                    visible = !hide,
                ) {
                    CheersBottomBar(
                        unreadChatCount = uiState.unreadChatCount,
                        picture = uiState.user?.picture ?: "",
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            appState.navController.navigate(route)
                        },
                    )
                }
            },
        ) { innerPadding ->
            val padding = if (hide)
                0.dp
            else
                innerPadding.calculateBottomPadding()
            AnimatedNavHost(
                modifier = Modifier.padding(bottom = padding),
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
                )
            }
        }
    }
}