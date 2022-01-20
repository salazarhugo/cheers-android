package com.salazar.cheers.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import com.salazar.cheers.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Profile screen.
 *
 * @param profileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current
    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = profileViewModel::refresh,
        onStatClicked = { statName, username ->  navActions.navigateToProfileStats(statName, username) },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onLikeClicked = profileViewModel::toggleLike,
        onEditProfileClicked = { navActions.navigateToEditProfile() },
        onSettingsClicked = navActions.navigateToSettings,
        navigateToProfileMoreSheet = navActions.navigateToProfileMoreSheet,
        onWebsiteClicked = { uriHandler.openUri(it) }
    )
}