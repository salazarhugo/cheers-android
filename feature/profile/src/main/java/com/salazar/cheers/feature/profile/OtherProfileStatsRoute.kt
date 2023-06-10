package com.salazar.cheers.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

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
    val uiState by otherProfileStatsViewModel.uiState.collectAsStateWithLifecycle()

    OtherProfileStatsScreen(
        uiState = uiState,
        onSwipeRefresh = otherProfileStatsViewModel::onSwipeRefresh,
        onFollowToggle = otherProfileStatsViewModel::toggleFollow,
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = navActions.navigateBack,
        onStoryClick = { navActions.navigateToStoryWithUserId(it) },
    )
}