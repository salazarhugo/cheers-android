package com.salazar.cheers.navigation

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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatScreen
import com.salazar.cheers.feature.chat.ui.screens.create_chat.CreateChatScreen
import com.salazar.cheers.feature.comment.comments.PostCommentsScreen
import com.salazar.cheers.feature.comment.replies.RepliesScreen
import com.salazar.cheers.feature.create_note.CreateNoteScreen
import com.salazar.cheers.feature.create_post.CreatePost
import com.salazar.cheers.feature.edit_profile.navigation.EditProfileGraph
import com.salazar.cheers.feature.parties.detail.PartyDetailScreen
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.bottombar.CheersBottomBar
import com.salazar.cheers.ui.main.camera.CameraScreen
import com.salazar.cheers.ui.main.party.create.CreatePartyGraph

@Composable
fun CheersNavGraph(
    uiState: CheersUiState.Initialized,
    appState: CheersAppState,
) {
    val passcodeEnabled = uiState.settings.passcodeEnabled

    val startDestination = remember {
        when (passcodeEnabled) {
            true -> PasscodeNavGraph
            false -> MainNavGraph
        }
    }

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val routesToHideBottomBar = listOf(
        AuthNavGraph,
        SettingsNavGraph,
        CreatePartyGraph,
        CreatePost(),
        ChatScreen(),
        CreateChatScreen,
        PostCommentsScreen(),
        RepliesScreen(),
        EditProfileGraph,
        CameraScreen,
        CreateNoteScreen,
        PartyDetailScreen(""),
    )

    val hide = currentDestination?.hierarchy?.any { destination ->
        routesToHideBottomBar.any {
            destination.hasRoute(route = it::class)
        }
    } == true

    ModalBottomSheetLayout(
        bottomSheetNavigator = appState.bottomSheetNavigator,
        sheetShape = MaterialTheme.shapes.extraLarge,
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
                settingsNavGraph(
                    appState = appState,
                )

                authNavGraph(
                    navController = appState.navController,
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