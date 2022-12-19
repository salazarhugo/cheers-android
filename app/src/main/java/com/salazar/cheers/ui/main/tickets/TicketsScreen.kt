package com.salazar.cheers.ui.main.tickets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.compose.EmptyActivity
import com.salazar.cheers.compose.share.ErrorMessage
import com.salazar.cheers.compose.share.Toolbar
import com.salazar.cheers.internal.Ticket
import com.salazar.cheers.ui.settings.*

@Composable
fun TicketsScreen(
    uiState: TicketsUiState,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = {},
                title = "Tickets",
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            TicketList(tickets = uiState.tickets)
        }
    }
}

@Composable
fun TicketList(tickets: List<Ticket>) {
    if (tickets.isEmpty())
        EmptyActivity()

    LazyColumn() {
        items(tickets) { ticket ->
            TicketItem(ticket = ticket)
        }
    }
}

@Composable
fun TicketItem(ticket: Ticket) {
    Text(text = ticket.name)
}