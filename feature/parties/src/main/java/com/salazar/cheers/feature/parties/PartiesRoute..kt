package com.salazar.cheers.feature.parties

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PartiesRoute(
    viewModel: PartiesViewModel = hiltViewModel(),
    navigateToPartyDetail: (String) -> Unit,
    navigateToPartyMoreSheet: (String) -> Unit,
    navigateToTickets: () -> Unit,
    navigateToCreateParty: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PartiesScreen(
        uiState = uiState,
        onPartyClicked = navigateToPartyDetail,
        onQueryChange = viewModel::onQueryChange,
        onMoreClick = navigateToPartyMoreSheet,
        onSwipeToRefresh = viewModel::onSwipeToRefresh,
        navigateToTickets = navigateToTickets,
        onCreatePartyClick = navigateToCreateParty,
    )
}