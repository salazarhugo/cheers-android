package com.salazar.cheers.ui.settings.password

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the CreatePassword screen.
 *
 * @param createPasswordViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CreatePasswordRoute(
    createPasswordViewModel: CreatePasswordViewModel = hiltViewModel(),
    navActions: CheersNavigationActions
) {
    val uiState by createPasswordViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.done) {
        if (uiState.done)
            navActions.navigateBack()
    }

    CreatePasswordScreen(
        uiState = uiState,
        onBackPressed = { navActions.navigateBack() },
        onPasswordChange = createPasswordViewModel::onPasswordChange,
        onCreatePassword = {
            createPasswordViewModel.onCreatePassword()
        },
    )
}