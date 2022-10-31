package com.salazar.cheers.ui.main.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Events screen.
 *
 * @param eventsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun EventsRoute(
    eventsViewModel: EventsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by eventsViewModel.uiState.collectAsState()

    EventsScreen(
        uiState = uiState,
        onEventClicked = {
            navActions.navigateToEventDetail(it)
        },
        onQueryChange = eventsViewModel::onQueryChange,
        onInterestedToggle = eventsViewModel::onInterestedToggle,
        onGoingToggle = eventsViewModel::onGoingToggle,
        onMoreClick = {
            navActions.navigateToEventMoreSheet(it)
        },
        onCreateEventClick = {
            navActions.navigateToAddEvent()
        },
    )
}
