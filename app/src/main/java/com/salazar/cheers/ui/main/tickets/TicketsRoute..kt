package com.salazar.cheers.ui.main.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Tickets screen.
 *
 * @param ticketsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun TicketsRoute(
    ticketsViewModel: TicketsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by ticketsViewModel.uiState.collectAsState()

    TicketsScreen(
        uiState = uiState,
    )
}
