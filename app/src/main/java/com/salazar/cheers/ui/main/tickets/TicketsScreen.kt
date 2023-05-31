package com.salazar.cheers.ui.main.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.core.data.internal.Ticket
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState

@Composable
fun TicketsScreen(
    uiState: TicketsUiState,
    onTicketsUIAction: (TicketsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = { onTicketsUIAction(TicketsUIAction.OnBackPressed)},
                title = stringResource(id = R.string.tickets),
            )
        },
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = { onTicketsUIAction(TicketsUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
            ) {
                val tickets = uiState.tickets
                if (tickets == null)
                    LoadingScreen()
                else
                    TicketList(
                        tickets = tickets,
                        onTicketsUIAction = onTicketsUIAction,
                    )
            }
        }
    }
}

@Composable
fun TicketList(
    tickets: List<Ticket>,
    onTicketsUIAction: (TicketsUIAction) -> Unit,
) {
    if (tickets.isEmpty())
        com.salazar.cheers.core.share.ui.EmptyActivity()

    LazyColumn() {
        items(tickets) { ticket ->
            TicketItem(
                ticket = ticket,
                onTicketClick = { onTicketsUIAction(TicketsUIAction.OnTicketClick(it)) },
            )
        }
    }
}

@Composable
fun TicketItem(
    ticket: Ticket,
    onTicketClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { onTicketClick(ticket.id) },
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = ticket.partyName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = ticket.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ticket price",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = (ticket.price / 100).toString() + " EUR",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}