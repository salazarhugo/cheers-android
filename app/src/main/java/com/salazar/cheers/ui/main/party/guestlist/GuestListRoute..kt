package com.salazar.cheers.ui.main.party.guestlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GuestListRoute(
    viewModel: GuestListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val goingState by viewModel.goingState.collectAsStateWithLifecycle()
    val interestedState by viewModel.interestedState.collectAsStateWithLifecycle()

    GuestListScreen(
        uiState = uiState,
        goingState = goingState,
        interestedState = interestedState,
        onSwipeRefresh = viewModel::onSwipeRefresh,
        onDismiss = navigateBack,
        onUserClick = navigateToOtherProfile,
    )
}
