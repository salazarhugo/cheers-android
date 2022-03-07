package com.salazar.cheers.ui.main.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Profile stats screen.
 *
 * @param profileStatsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ProfileStatsRoute(
    profileStatsViewModel: ProfileStatsViewModel,
    navActions: CheersNavigationActions,
    username: String,
    verified: Boolean,
) {
    val uiState by profileStatsViewModel.uiState.collectAsState()
    ProfileStatsScreen(
        uiState = uiState,
        username = username,
        verified = verified,
        onUnfollow = { profileStatsViewModel.unfollow(it) },
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = navActions.navigateBack,
    )
}