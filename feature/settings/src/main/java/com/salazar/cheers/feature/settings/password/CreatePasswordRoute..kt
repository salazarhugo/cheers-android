package com.salazar.cheers.feature.settings.password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

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
    val uiState by createPasswordViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.done) {
        if (uiState.done)
            navActions.navigateBack()
    }

    CreatePasswordScreen(
        uiState = uiState,
        onBackPressed = { navActions.navigateBack() },
        onPasswordChange = createPasswordViewModel::onPasswordChange,
        onCreatePassword = {
            createPasswordViewModel.onCreatePassword {
                navActions.navigateBack()
            }
        },
    )
}
