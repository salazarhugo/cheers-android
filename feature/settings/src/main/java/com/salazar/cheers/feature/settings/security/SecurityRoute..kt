package com.salazar.cheers.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun SecurityRoute(
    securityViewModel: SecurityViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPassword: (Boolean) -> Unit,
    navigateToPasscode: () -> Unit,
) {
    val uiState by securityViewModel.uiState.collectAsStateWithLifecycle()
//    val authResultLauncher =
//        rememberLauncherForActivityResult(
//            contract = com.salazar.cheers.data.auth.AuthResultContract(),
//            securityViewModel::onResult
//        )

    SecurityScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
        onUnlink = {},
        onAddPassword = navigateToPassword,
        onLink = {
//                if (it == GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)
//                    authResultLauncher.launch(1)
        },
        onPasscodeClick = navigateToPasscode,
    )
}
