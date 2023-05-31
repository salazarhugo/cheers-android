package com.salazar.cheers.ui.main.party.guestlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the GuestList screen.
 *
 * @param guestListViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun GuestListRoute(
    guestListViewModel: GuestListViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by guestListViewModel.uiState.collectAsStateWithLifecycle()

    GuestListScreen(
        uiState = uiState,
        onSwipeRefresh = guestListViewModel::onSwipeRefresh,
        onDismiss = {
            navActions.navigateBack()
        },
        onUserClick = {
            navActions.navigateToOtherProfile(it)
        }
    )
}
