package com.salazar.cheers.feature.parties

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CategoryTitle
import com.salazar.cheers.core.ui.PartiesTopBar
import com.salazar.cheers.core.ui.PartyItem
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.data.party.Party


@Composable
fun PartiesScreen(
    uiState: PartiesUiState,
    onPartyClicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onSwipeToRefresh: () -> Unit,
    navigateToTickets: () -> Unit,
) {
    Scaffold(
        topBar = {
            PartiesTopBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onTicketsClick = navigateToTickets,
            )
        },
    ) {
        val parties = uiState.parties

        if (parties == null)
            com.salazar.cheers.core.share.ui.LoadingScreen()
        else
            SwipeToRefresh(
                onRefresh = onSwipeToRefresh,
                state = rememberSwipeToRefreshState(uiState.isRefreshing),
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            ) {
                PartyList(
                    events = parties,
                    onPartyClicked = onPartyClicked,
                    onMoreClick = onMoreClick,
                )
            }
    }
}

@Composable
fun PartyList(
    events: List<Party>,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
//        item {
//            CategoryTitle(
//                modifier = Modifier.padding(16.dp),
//                text = "On display",
//            )
//        }
//        item {
//            LazyRow(
//            ) {
//                items(events, key = { it.id }) { event ->
//                    PartyItem(
//                        modifier = Modifier.width(260.dp),
//                        party = event,
//                        onPartyClicked = onPartyClicked,
//                        onMoreClick = onMoreClick,
//                    )
//                }
//            }
//        }
//        item {
//            CategoryTitle(
//                modifier = Modifier.padding(16.dp),
//                text = stringResource(id = R.string.on_display),
//            )
//        }
        items(events, key = { it.id }) { event ->
            PartyItem(
                party = event,
                onPartyClicked = onPartyClicked,
                onMoreClick = onMoreClick,
            )
        }
    }
}