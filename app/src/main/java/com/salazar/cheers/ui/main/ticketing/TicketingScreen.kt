package com.salazar.cheers.ui.main.ticketing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.compose.DividerM3
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.dateTimeFormatter


@Composable
fun TicketingScreen(
    uiState: TicketingUiState,
    onBackPressed: () -> Unit,
    onSwipeRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
        },
    ) {
        SwipeToRefresh(
            onRefresh = onSwipeRefresh,
            state = rememberSwipeToRefreshState(isRefreshing = false),
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
        ) {
            if (uiState.party == null)
                LoadingScreen()
            else
                TicketingHeader(
                    party = uiState.party,
                )
            Tickets(
                tickets = uiState.tickets.orEmpty(),
            )
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Next",
                )
            }
        }
    }
}

@Composable
fun Tickets(
    tickets: List<TicketingTicket>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            tickets.forEach { ticket ->
                Ticket(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    ticket = ticket
                )
                DividerM3()
            }
        }
    }
}

@Composable
fun TicketingHeader(
    party: Party,
) {
    AsyncImage(
        model = party.imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Ticketing ${party.name}",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "by ${party.hostName}",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Event, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = dateTimeFormatter(timestamp = party.startDate),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}


@Composable
fun Ticket(
    modifier: Modifier = Modifier,
    ticket: TicketingTicket,
    onQuantityChange: (Int) -> Unit = {},
) {
    var quantity by remember { mutableStateOf(0)}

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = ticket.title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "${ticket.price/100} $",
            style = MaterialTheme.typography.headlineLarge,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {},
            ) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {},
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
fun TicketingOption() {

}