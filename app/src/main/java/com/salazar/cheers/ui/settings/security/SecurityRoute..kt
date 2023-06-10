package com.salazar.cheers.ui.settings.security

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.GoogleAuthProvider
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Security screen.
 *
 * @param securityViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SecurityRoute(
    securityViewModel: SecurityViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by securityViewModel.uiState.collectAsStateWithLifecycle()
    val authResultLauncher =
        rememberLauncherForActivityResult(
            contract = com.salazar.cheers.data.auth.AuthResultContract(),
            securityViewModel::onResult
        )

    if (uiState.firebaseUser == null)
        com.salazar.cheers.core.share.ui.LoadingScreen()
    else
        SecurityScreen(
            uiState = uiState,
            onBackPressed = { navActions.navigateBack() },
            onUnlink = securityViewModel::onUnlink,
            onAddPassword = { navActions.navigateToPassword(it) },
            onLink = {
                if (it == GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
                    authResultLauncher.launch(1)
            },
        )
}
