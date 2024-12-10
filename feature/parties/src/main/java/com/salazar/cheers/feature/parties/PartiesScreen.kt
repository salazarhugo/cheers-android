package com.salazar.cheers.feature.parties

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.PartiesTopBar
import com.salazar.cheers.core.ui.item.party.PartyList
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState


@Composable
fun PartiesScreen(
    uiState: PartiesUiState,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onSwipeToRefresh: () -> Unit,
    navigateToTickets: () -> Unit,
    onCreatePartyClick: () -> Unit,
    onChangeCityClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            PartiesTopBar(
                onTicketsClick = navigateToTickets,
                onCreatePartyClick = onCreatePartyClick,
            )
        },
    ) {
        val parties = uiState.parties

        if (parties == null) {
            LoadingScreen()
        } else {
            SwipeToRefresh(
                onRefresh = onSwipeToRefresh,
                state = rememberSwipeToRefreshState(uiState.isRefreshing),
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            ) {
                PartyList(
                    events = parties,
                    onPartyClicked = onPartyClicked,
                    onMoreClick = onMoreClick,
                    onChangeCityClick = onChangeCityClick,
                    onCreatePartyClick = onCreatePartyClick,
                )
            }
        }
    }
}

