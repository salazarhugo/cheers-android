package com.salazar.cheers.ui.main.ticketing

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Ticketing screen.
 *
 * @param ticketingViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun TicketingRoute(
    ticketingViewModel: TicketingViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by ticketingViewModel.uiState.collectAsState()

    TicketingScreen(
        uiState = uiState,
        modifier = Modifier.navigationBarsPadding(),
        onSwipeRefresh = ticketingViewModel::onSwipeRefresh,
        onBackPressed = { navActions.navigateBack() },
    )
}