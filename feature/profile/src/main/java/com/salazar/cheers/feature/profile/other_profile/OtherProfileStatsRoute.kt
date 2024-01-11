package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

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