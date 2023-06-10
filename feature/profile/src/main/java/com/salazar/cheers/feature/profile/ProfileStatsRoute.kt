package com.salazar.cheers.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

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
    val uiState by profileStatsViewModel.uiState.collectAsStateWithLifecycle()
    ProfileStatsScreen(
        uiState = uiState,
        username = username,
        verified = verified,
        onFollowToggle = profileStatsViewModel::toggleFollow,
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = navActions.navigateBack,
        onSwipeRefresh = profileStatsViewModel::onSwipeRefresh,
        onStoryClick = { navActions.navigateToStoryWithUserId(it) },
    )
}