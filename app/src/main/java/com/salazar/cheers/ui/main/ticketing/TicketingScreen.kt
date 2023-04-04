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
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.dateTimeFormatter
import com.salazar.cheers.ui.compose.DividerM3
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.share.ButtonWithLoading
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.main.party.PriceTag

@Composable
fun TicketingScreen(
    uiState: TicketingUiState,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onSwipeRefresh: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            Basket(
                total = 1113,
                onClick = {},
            )
        },
    ) {
        SwipeToRefresh(
            onRefresh = onSwipeRefresh,
            state = rememberSwipeToRefreshState(uiState.isLoading),
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
            modifier = Modifier.padding(vertical = 16.dp),
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
        model = party.bannerUrl,
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
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "by ${party.hostName}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Event, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = dateTimeFormatter(timestamp = party.startDate.toLong()),
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
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = ticket.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.titleMedium,
        )
        PriceTag(
            price = ticket.price,
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

@Composable
fun Basket(
    total: Int,
    onClick: () -> Unit,
) {
    ButtonWithLoading(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = "View basket ${total/100} €",
        isLoading = false,
        onClick = onClick,
    )
}