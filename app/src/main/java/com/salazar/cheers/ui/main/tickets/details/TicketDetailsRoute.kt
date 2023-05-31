package com.salazar.cheers.ui.main.tickets.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import com.salazar.cheers.ui.compose.ForceBrightness

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
    val uiState by ticketDetailsViewModel.uiState.collectAsStateWithLifecycle()

    ForceBrightness()

    TicketDetailsScreen(
        uiState = uiState,
    )
}
