package com.salazar.cheers.ui.main.party

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CategoryTitle
import com.salazar.cheers.core.ui.PartiesTopBar
import com.salazar.cheers.core.ui.PartyItem
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.data.party.Party


@Composable
fun PartiesScreen(
    uiState: EventsUiState,
    onEventClicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            PartiesTopBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
            )
        },
    ) {
        val parties = uiState.parties

        if (parties == null)
            com.salazar.cheers.core.share.ui.LoadingScreen()
        else
            SwipeToRefresh(
                onRefresh = {},
                state = rememberSwipeToRefreshState(uiState.isLoading),
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            ) {
                EventList(
                    events = parties,
                    onEventClicked = onEventClicked,
                    onMoreClick = onMoreClick,
                )
            }
    }
}

@Composable
fun EventList(
    events: List<Party>,
    onEventClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item {
            CategoryTitle(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.on_display),
            )
        }
        item {
            LazyRow(
            ) {
                items(events, key = { it.id }) { event ->
                    PartyItem(
                        modifier = Modifier.width(260.dp),
                        party = event,
                        onEventClicked = onEventClicked,
                        onMoreClick = onMoreClick,
                    )
                }
            }
        }
//        item {
//            CategoryTitle(
//                modifier = Modifier.padding(16.dp),
//                text = stringResource(id = R.string.on_display),
//            )
//        }
        items(events, key = { it.id }) { event ->
            PartyItem(
                party = event,
                onEventClicked = onEventClicked,
                onMoreClick = onMoreClick,
            )
        }
    }
}