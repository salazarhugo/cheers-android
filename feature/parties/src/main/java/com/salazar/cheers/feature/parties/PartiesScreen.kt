package com.salazar.cheers.feature.parties

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.EmptyFeed
import com.salazar.cheers.core.ui.PartiesTopBar
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.core.model.Party


@Composable
fun PartiesScreen(
    uiState: PartiesUiState,
    onPartyClicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onSwipeToRefresh: () -> Unit,
    navigateToTickets: () -> Unit,
    onCreatePartyClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            PartiesTopBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onTicketsClick = navigateToTickets,
                onCreatePartyClick = onCreatePartyClick,
            )
        },
    ) {
        val parties = uiState.parties

        if (parties == null)
            LoadingScreen()
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
        emptyParties(events.isEmpty())

        items(events, key = { it.id }) { event ->
            PartyItem(
                party = event,
                onPartyClicked = onPartyClicked,
                onMoreClick = onMoreClick,
            )
        }
    }
}

private fun LazyListScope.emptyParties(isEmpty: Boolean) {
    if (!isEmpty)
        return
    item {
        EmptyFeed()
    }
}
