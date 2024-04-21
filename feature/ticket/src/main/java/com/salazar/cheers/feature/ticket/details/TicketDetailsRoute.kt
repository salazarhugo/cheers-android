package com.salazar.cheers.feature.ticket.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ForceBrightness

@Composable
fun TicketDetailsRoute(
    viewModel: TicketDetailsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForceBrightness()

    TicketDetailsScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
    )
}
