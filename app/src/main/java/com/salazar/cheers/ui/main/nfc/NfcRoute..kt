package com.salazar.cheers.ui.main.nfc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Nfc screen.
 *
 * @param nfcViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun NfcRoute(
    nfcViewModel: NfcViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by nfcViewModel.uiState.collectAsStateWithLifecycle()

    NfcScreen(
//        uiState = uiState,
    )
}
