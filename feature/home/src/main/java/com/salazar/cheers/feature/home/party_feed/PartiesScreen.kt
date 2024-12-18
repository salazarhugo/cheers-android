package com.salazar.cheers.feature.home.party_feed

import androidx.compose.runtime.Composable
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.ui.item.party.PartyList

@Composable
fun PartyFeedScreen(
    isLoading: Boolean,
    isLoadingMore: Boolean,
    parties: List<Party>?,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onChangeCityClick: () -> Unit,
    onCreatePartyClick: () -> Unit,
    onLoadMore: (Int) -> Unit,
) {
    PartyList(
        isLoading = isLoading,
        isLoadingMore = isLoadingMore,
        parties = parties,
        onPartyClick = onPartyClicked,
        onMoreClick = onMoreClick,
        onChangeCityClick = onChangeCityClick,
        onCreatePartyClick = onCreatePartyClick,
        onLoadMore = onLoadMore,
    )
}