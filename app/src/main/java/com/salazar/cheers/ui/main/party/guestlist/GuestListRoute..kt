package com.salazar.cheers.ui.main.party.guestlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GuestListRoute(
    guestListViewModel: GuestListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by guestListViewModel.uiState.collectAsStateWithLifecycle()

    GuestListScreen(
        uiState = uiState,
        onSwipeRefresh = guestListViewModel::onSwipeRefresh,
        onDismiss = navigateBack,
        onUserClick = navigateToOtherProfile,
    )
}
