package com.salazar.cheers.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun ProfileStatsRoute(
    profileStatsViewModel: ProfileStatsViewModel,
    navActions: CheersNavigationActions,
    username: String,
    verified: Boolean,
    premium: Boolean,
) {
    val uiState by profileStatsViewModel.uiState.collectAsStateWithLifecycle()

    ProfileStatsScreen(
        uiState = uiState,
        username = username,
        verified = verified,
        premium = premium,
        onFollowToggle = profileStatsViewModel::toggleFollow,
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = navActions.navigateBack,
        onSwipeRefresh = profileStatsViewModel::onSwipeRefresh,
        onStoryClick = { navActions.navigateToStoryWithUserId(it) },
    )
}