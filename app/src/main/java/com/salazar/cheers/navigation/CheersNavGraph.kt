package com.salazar.cheers.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.core.ui.theme.GreySheet
import com.salazar.cheers.core.ui.ui.CheersDestinations
import com.salazar.cheers.core.ui.ui.MainDestinations
import com.salazar.cheers.feature.chat.ui.screens.messages.Messages
import com.salazar.cheers.feature.create_note.createNoteNavigationRoute
import com.salazar.cheers.feature.create_post.CreatePost
import com.salazar.cheers.feature.parties.partiesNavigationRoute
import com.salazar.cheers.feature.signin.signInNavigationRoute
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.bottombar.CheersBottomBar
import com.salazar.cheers.ui.main.party.create.CreatePartyGraph

@Composable
fun CheersNavGraph(
    uiState: CheersUiState.Initialized,
    appState: CheersAppState,
) {
    val passcodeEnabled = uiState.settings.passcodeEnabled

    val startDestination =
        remember {
            when (passcodeEnabled) {
                true -> PasscodeNavGraph
                false -> MainNavGraph
            }
        }

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute =
        navBackStackEntry?.destination?.route ?: partiesNavigationRoute
    val navActions = appState.navActions

    val hide =
        navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.AUTH_ROUTE } == true
                || navBackStackEntry?.destination?.hierarchy?.any { it.route == CheersDestinations.SETTING_ROUTE } == true
                || navBackStackEntry?.destination?.hierarchy?.any { it.route?.contains(CreatePartyGraph.toString()) == true } == true
                || currentRoute.contains(MainDestinations.STORY_ROUTE)
                || currentRoute.contains(Messages.toString())
                || currentRoute.contains(MainDestinations.CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.NEW_CHAT_ROUTE)
                || currentRoute.contains(MainDestinations.ROOM_DETAILS)
                || currentRoute.contains(MainDestinations.POST_COMMENTS)
                || currentRoute.contains(MainDestinations.COMMENT_REPLIES)
                || currentRoute.contains(MainDestinations.COMMENT_MORE_SHEET)
                || currentRoute.contains(MainDestinations.COMMENT_DELETE)
                || currentRoute.contains(MainDestinations.TICKETING_ROUTE)
                || currentRoute.contains(CreatePost.toString())
                || currentRoute.contains(createNoteNavigationRoute)
                || currentRoute.contains(MainDestinations.EDIT_PROFILE_ROUTE)
                || currentRoute.contains(MainDestinations.CAMERA_ROUTE)
                || currentRoute.contains(CheersDestinations.PASSCODE_ROUTE)

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
                if (hide) return@Scaffold

                CheersBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        appState.navController.navigate(route)
                    },
                )
            },
        ) { innerPadding ->
            val padding = when (hide) {
                true -> 0.dp
                false -> innerPadding.calculateBottomPadding()
            }
            NavHost(
                modifier = Modifier.padding(bottom = padding),
                navController = appState.navController,
                startDestination = startDestination,
            ) {
                settingNavGraph(
                    appState = appState,
                )

                authNavGraph(
                    navActions = navActions,
                    navController = appState.navController,
                    startDestination = signInNavigationRoute,
                )

                mainNavGraph(
                    appState = appState,
                    appSettings = uiState.settings,
                )

                passcodeNavGraph(
                    navController = appState.navController,
                )
            }
        }
    }
}