package com.salazar.cheers.feature.parties.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.modifier.rememberFlowWithLifecycle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
    val snackbarHostState = remember { SnackbarHostState() }
    val sideEffect = rememberFlowWithLifecycle(
        flow = viewModel.sideEffect,
        lifecycle = LocalLifecycleOwner.current.lifecycle,
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(sideEffect) {
        sideEffect.onEach {
            scope.launch {
                val message = it
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                )
            }
        }.launchIn(this)
    }

    if (uiState is PartyDetailUiState.HasParty) {
        PartyDetailScreen(
            snackbarHostState = snackbarHostState,
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
                viewModel.deleteParty {
                    navigateBack()
                }
            },
            onGoingCountClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            },
            onInterestedCountClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            },
            onTicketingClick = navigateToTicketing,
            onAnswersClick = {
                val eventId = uiState.party.id
                navigateToGuestList(eventId)
            }
        )
    } else {
        PartyDetailLoadingScreen()
    }
}