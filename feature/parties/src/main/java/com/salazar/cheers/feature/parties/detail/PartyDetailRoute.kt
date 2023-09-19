package com.salazar.cheers.feature.parties.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.common.util.copyToClipboard

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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState is PartyDetailUiState.HasParty)
        PartyDetailScreen(
            uiState = uiState as PartyDetailUiState.HasParty,
            onMapClick = navigateToMap,
            onUserClicked = navigateToOtherProfile,
            onWatchStatusChange = viewModel::onWatchStatusChange,
            onCopyLink = {
                val eventId = (uiState as PartyDetailUiState.HasParty).party.id
//                FirebaseDynamicLinksUtil.createShortLink("event/$eventId")
//                    .addOnSuccessListener { shortLink ->
//                        context.copyToClipboard(shortLink.shortLink.toString())
//                    }
                navigateBack()
            },
            onEditClick = {
                val eventId = (uiState as PartyDetailUiState.HasParty).party.id
                navigateToEditParty(eventId)
            },
            onDeleteClick = {
                viewModel.deleteParty()
                navigateBack()
            },
            onGoingCountClick = {
                val eventId = (uiState as PartyDetailUiState.HasParty).party.id
                navigateToGuestList(eventId)
            },
            onInterestedCountClick = {
                val eventId = (uiState as PartyDetailUiState.HasParty).party.id
                navigateToGuestList(eventId)
            },
            onTicketingClick = {
                navigateToTicketing(it)
            },
        )
    else
        com.salazar.cheers.core.share.ui.LoadingScreen()
}