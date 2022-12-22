package com.salazar.cheers.ui.main.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

/**
 * Stateful composable that displays the Navigation route for the Share screen.
 *
 * @param shareViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ShareRoute(
    appState: CheersAppState,
    shareViewModel: ShareViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by shareViewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

    ShareScreen(
        uiState = uiState,
        onShareUIAction = { action ->
            when (action) {
                ShareUIAction.OnBackPressed -> navActions.navigateBack()
                is ShareUIAction.OnMessageChange -> shareViewModel.onMessageChange(action.message)
                else -> {}
            }
        },
    )
}