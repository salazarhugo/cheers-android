package com.salazar.cheers.ui.event.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Event detail screen.
 *
 * @param eventDetailViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun EventDetailRoute(
    eventDetailViewModel: EventDetailViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by eventDetailViewModel.uiState.collectAsState()

    if (uiState is EventDetailUiState.HasEvent)
        EventDetailScreen(
            uiState = uiState as EventDetailUiState.HasEvent,
        )
    else
        LoadingScreen()
}