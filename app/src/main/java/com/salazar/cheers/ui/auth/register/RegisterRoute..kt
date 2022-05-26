package com.salazar.cheers.ui.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Register screen.
 *
 * @param registerViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun RegisterRoute(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by registerViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if(!uiState.success) return@LaunchedEffect
        navActions.navigateToMain()
    }

    RegisterScreen(
        uiState = uiState,
        onRegisterClick = registerViewModel::register,
        onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
    )
}
