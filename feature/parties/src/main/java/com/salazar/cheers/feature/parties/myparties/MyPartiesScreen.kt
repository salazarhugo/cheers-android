package com.salazar.cheers.feature.parties.myparties

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.ui.components.filters.Filters
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent
import com.salazar.cheers.core.ui.item.party.PartyList
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState


@Composable
fun MyPartiesScreen(
    uiState: MyPartiesUiState,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onSwipeToRefresh: () -> Unit,
    navigateToTickets: () -> Unit,
    onCreatePartyClick: () -> Unit,
    onChangeCityClick: () -> Unit,
    onLoadMore: (Int) -> Unit,
    navigateBack: () -> Unit,
    onFilterClick: (Filter) -> Unit,
) {
    Scaffold(
        topBar = {
            MyPartiesTopBar(
                onBackPressed = navigateBack,
            )
        },
    ) {
        val parties = uiState.parties
        val filters = uiState.filters

        SwipeToRefresh(
            onRefresh = onSwipeToRefresh,
            state = rememberSwipeToRefreshState(uiState.isRefreshing),
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            Filters(
                filters = filters,
                onFilterClick = onFilterClick,
            )
            PartyList(
                parties = parties,
                isLoading = uiState.isLoading,
                isLoadingMore = uiState.isLoadingMore,
                onPartyClick = onPartyClicked,
                onMoreClick = onMoreClick,
                onChangeCityClick = onChangeCityClick,
                onCreatePartyClick = onCreatePartyClick,
                onLoadMore = onLoadMore,
                onMyPartiesClick = {},
                myParties = emptyList(),
                emptyScreen = {
                    MessageScreenComponent(
                        modifier = Modifier.padding(16.dp),
                        title = "No parties found",
                    )
                }
            )
        }
    }
}

