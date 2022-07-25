package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Profile stats screen.
 *
 * @param otherProfileStatsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun OtherProfileStatsRoute(
    otherProfileStatsViewModel: OtherProfileStatsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by otherProfileStatsViewModel.uiState.collectAsState()

    OtherProfileStatsScreen(
        uiState = uiState,
        onSwipeRefresh = otherProfileStatsViewModel::onSwipeRefresh,
        onFollowToggle = otherProfileStatsViewModel::toggleFollow,
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = navActions.navigateBack,
        onStoryClick = { navActions.navigateToStoryWithUserId(it) },
    )
}