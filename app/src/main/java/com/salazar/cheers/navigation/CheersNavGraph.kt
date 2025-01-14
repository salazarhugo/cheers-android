package com.salazar.cheers.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.core.ui.navigation.PartyDetailScreen
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatScreen
import com.salazar.cheers.feature.chat.ui.screens.create_chat.CreateChatScreen
import com.salazar.cheers.feature.chat.ui.screens.mediapreview.MediaPreviewScreen
import com.salazar.cheers.feature.chat.ui.screens.room.ChatInfoScreen
import com.salazar.cheers.feature.comment.comments.PostCommentsScreen
import com.salazar.cheers.feature.comment.replies.RepliesScreen
import com.salazar.cheers.feature.create_note.CreateNoteScreen
import com.salazar.cheers.feature.create_post.CreatePostGraph
import com.salazar.cheers.feature.edit_profile.navigation.EditProfileGraph
import com.salazar.cheers.feature.settings.SettingsNavGraph
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.compose.bottombar.CheersBottomBar
import com.salazar.cheers.ui.main.camera.CameraScreen
import com.salazar.cheers.ui.main.party.create.CreatePartyGraph
import com.softimpact.feature.passcode.passcode.Passcode

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
        CreatePostGraph,
        ChatScreen(),
        CreateChatScreen,
        PostCommentsScreen(),
        RepliesScreen(),
        EditProfileGraph,
        CameraScreen,
        CreateNoteScreen,
        PartyDetailScreen(""),
        ChatInfoScreen(""),
        MediaPreviewScreen(""),
        Passcode,
    )

    val hide = currentDestination?.hierarchy?.any { destination ->
        routesToHideBottomBar.any {
            destination.hasRoute(route = it::class)
        }
    } == true

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
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