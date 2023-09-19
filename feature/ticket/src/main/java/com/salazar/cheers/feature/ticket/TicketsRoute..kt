package com.salazar.cheers.feature.ticket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TicketsRoute(
    viewModel: TicketsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToTicketDetails: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketsScreen(
        uiState = uiState,
        onTicketsUIAction = { action ->
            when (action) {
                TicketsUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                is TicketsUIAction.OnTicketClick -> {
                    navigateToTicketDetails(action.ticketId)
                }
                TicketsUIAction.OnBackPressed -> navigateBack()
            }
        }
    )
}
