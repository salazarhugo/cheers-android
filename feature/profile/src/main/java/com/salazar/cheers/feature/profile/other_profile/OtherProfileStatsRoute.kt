package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun OtherProfileStatsRoute(
    otherProfileStatsViewModel: OtherProfileStatsViewModel = hiltViewModel(),
    navigateToOtherProfile: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by otherProfileStatsViewModel.uiState.collectAsStateWithLifecycle()

    OtherProfileStatsScreen(
        uiState = uiState,
        onSwipeRefresh = otherProfileStatsViewModel::onSwipeRefresh,
        onFollowToggle = otherProfileStatsViewModel::toggleFollow,
        onUserClicked = navigateToOtherProfile,
        onBackPressed = navigateBack,
        onStoryClick = {},
    )
}