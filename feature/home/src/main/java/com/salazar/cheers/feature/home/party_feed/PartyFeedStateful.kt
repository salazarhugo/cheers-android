package com.salazar.cheers.feature.home.party_feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.parties.PartiesViewModel

@Composable
fun PartyFeedStateful(
    viewModel: PartiesViewModel = hiltViewModel(),
    navigateToPartyDetail: (String) -> Unit,
    navigateToPartyMoreSheet: (String) -> Unit,
    onChangeCityClick: () -> Unit,
    navigateToCreateParty: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PartyFeedScreen(
        isLoading = uiState.isLoading,
        isLoadingMore = uiState.isLoadingMore,
        parties = uiState.parties,
        onPartyClicked = navigateToPartyDetail,
        onMoreClick = navigateToPartyMoreSheet,
        onChangeCityClick = onChangeCityClick,
        onCreatePartyClick = navigateToCreateParty,
        onLoadMore = viewModel::onLoadMore,
    )
}