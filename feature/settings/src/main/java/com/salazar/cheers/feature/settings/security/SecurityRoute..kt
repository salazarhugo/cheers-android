package com.salazar.cheers.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecurityRoute(
    viewModel: SecurityViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPassword: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
    navigateToCreatePasscode: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val authResultLauncher =
//        rememberLauncherForActivityResult(
//            contract = com.salazar.cheers.data.auth.AuthResultContract(),
//            securityViewModel::onResult
//        )

    SecurityScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
        onUnlink = {},
        onAddPassword = {
            navigateToPassword()
        },
        onLink = {
//                if (it == GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
//                    authResultLauncher.launch(1)
        },
        onPasscodeClick = {
            if (uiState.passcodeEnabled)
                navigateToPasscodeSettings()
            else
                navigateToCreatePasscode()
        },
    )
}
