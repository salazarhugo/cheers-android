package com.salazar.cheers.ui.main.party.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.data.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.common.util.copyToClipboard

/**
 * Stateful composable that displays the Navigation route for the Event detail screen.
 *
 * @param eventDetailViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun EventDetailRoute(
    eventDetailViewModel: EventDetailViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by eventDetailViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState is EventDetailUiState.HasEvent)
        EventDetailScreen(
            uiState = uiState as EventDetailUiState.HasEvent,
            onMapClick = { navActions.navigateToMap() },
            onUserClicked = { navActions.navigateToOtherProfile(it) },
            onWatchStatusChange = eventDetailViewModel::onWatchStatusChange,
            onCopyLink = {
                val eventId = (uiState as EventDetailUiState.HasEvent).party.id
                FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
                    .addOnSuccessListener { shortLink ->
                        context.copyToClipboard(shortLink.shortLink.toString())
                    }
                navActions.navigateBack()
            },
            onEditClick = {
                val eventId = (uiState as EventDetailUiState.HasEvent).party.id
                navActions.navigateToEditEvent(eventId)
            },
            onDeleteClick = {
                eventDetailViewModel.deleteEvent()
                navActions.navigateBack()
            },
            onGoingCountClick = {
                val eventId = (uiState as EventDetailUiState.HasEvent).party.id
                navActions.navigateToGuestList(eventId)
            },
            onInterestedCountClick = {
                val eventId = (uiState as EventDetailUiState.HasEvent).party.id
                navActions.navigateToGuestList(eventId)
            },
            onTicketingClick = {
                navActions.navigateToTicketing(it)
            },
        )
    else
        com.salazar.cheers.core.share.ui.LoadingScreen()
}