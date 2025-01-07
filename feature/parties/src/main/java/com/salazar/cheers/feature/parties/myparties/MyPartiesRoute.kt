package com.salazar.cheers.feature.parties.myparties

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MyPartiesRoute(
    viewModel: MyPartiesViewModel = hiltViewModel(),
    navigateToPartyDetail: (String) -> Unit,
    navigateToPartyMoreSheet: (String) -> Unit,
    navigateToTickets: () -> Unit,
    navigateToCreateParty: () -> Unit,
    onChangeCityClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyPartiesScreen(
        uiState = uiState,
        onPartyClicked = navigateToPartyDetail,
        onMoreClick = navigateToPartyMoreSheet,
        onSwipeToRefresh = viewModel::onSwipeToRefresh,
        navigateToTickets = navigateToTickets,
        onCreatePartyClick = navigateToCreateParty,
        onChangeCityClick = onChangeCityClick,
        onLoadMore = viewModel::onLoadMore,
        navigateBack = navigateBack,
        onFilterClick = viewModel::onFilterClick,
    )
}