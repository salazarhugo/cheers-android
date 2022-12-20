package com.salazar.cheers.ui.main.tickets.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.compose.ForceBrightness
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.main.tickets.TicketsScreen
import com.salazar.cheers.ui.main.tickets.TicketsViewModel

/**
 * Stateful composable that displays the Navigation route for the Tickets screen.
 *
 * @param ticketDetailsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun TicketDetailsRoute(
    ticketDetailsViewModel: TicketDetailsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by ticketDetailsViewModel.uiState.collectAsState()

    ForceBrightness()

    TicketDetailsScreen(
        uiState = uiState,
    )
}
