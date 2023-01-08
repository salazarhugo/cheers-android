package com.salazar.cheers.ui.main.tickets

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
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.compose.EmptyActivity
import com.salazar.cheers.ui.compose.share.Toolbar
import com.salazar.cheers.internal.Ticket

@Composable
fun TicketsScreen(
    uiState: TicketsUiState,
    onTicketsUIAction: (TicketsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = { onTicketsUIAction(TicketsUIAction.OnBackPressed)},
                title = "Tickets",
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            TicketList(
                tickets = uiState.tickets,
                onTicketsUIAction = onTicketsUIAction,
            )
        }
    }
}

@Composable
fun TicketList(
    tickets: List<Ticket>,
    onTicketsUIAction: (TicketsUIAction) -> Unit,
) {
    if (tickets.isEmpty())
        EmptyActivity()

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