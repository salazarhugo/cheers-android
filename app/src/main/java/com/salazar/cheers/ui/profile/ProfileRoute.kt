package com.salazar.cheers.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    ProfileScreen(
        uiState = uiState,
        onSwipeRefresh = { profileViewModel.refresh() },
        onStatClicked = { statName, username ->  navActions.navigateToProfileStats(statName, username) },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onLikeClicked = { profileViewModel.toggleLike(it) },
        onEditProfileClicked = {},
        onSettingsClicked = navActions.navigateToSettings
    )
}