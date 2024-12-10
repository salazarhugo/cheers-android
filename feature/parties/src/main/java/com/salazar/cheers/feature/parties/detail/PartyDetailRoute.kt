package com.salazar.cheers.feature.parties.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PartyDetailRoute(
    viewModel: PartyDetailViewModel = hiltViewModel(),
    navigateToMap: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToTicketing: (String) -> Unit,
    navigateToEditParty: (String) -> Unit,
    navigateToGuestList: (String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    if (uiState is PartyDetailUiState.HasParty) {
        PartyDetailScreen(
            uiState = uiState,
            onMapClick = navigateToMap,
            onUserClicked = navigateToOtherProfile,
            onWatchStatusChange = viewModel::onWatchStatusChange,
            onCopyLink = {
                val eventId = uiState.party.id
//                FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
//                    .addOnSuccessListener { shortLink ->
//                        context.copyToClipboard(shortLink.shortLink.toString())
//                    }
                navigateBack()
            },
            onEditClick = {
                val eventId = uiState.party.id
                navigateToEditParty(eventId)
            },
            onDeleteClick = {
                viewModel.deleteParty()
                navigateBack()
            },
            onGoingCountClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            },
            onInterestedCountClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            },
            onTicketingClick = {
                navigateToTicketing(it)
            },
            onAnswersClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            }
        )
    }
    else {
        PartyDetailLoadingScreen()
    }
}