package com.salazar.cheers.ui.main.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

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
    val uiState by ticketsViewModel.uiState.collectAsStateWithLifecycle()

    TicketsScreen(
        uiState = uiState,
        onTicketsUIAction = { action ->
            when (action) {
                TicketsUIAction.OnSwipeRefresh -> ticketsViewModel.onSwipeRefresh()
                is TicketsUIAction.OnTicketClick -> {
                    navActions.navigateToTicketDetails(action.ticketId)
                }
                TicketsUIAction.OnBackPressed -> navActions.navigateBack()
            }
        }
    )
}
