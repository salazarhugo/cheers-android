package com.salazar.cheers.ui.main.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

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
    val uiState by eventsViewModel.uiState.collectAsStateWithLifecycle()

    EventsScreen(
        uiState = uiState,
        onEventClicked = {
            navActions.navigateToEventDetail(it)
        },
        onQueryChange = eventsViewModel::onQueryChange,
        onMoreClick = {
            navActions.navigateToEventMoreSheet(it)
        },
    )
}