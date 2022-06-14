package com.salazar.cheers.ui.auth.signin

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.salazar.cheers.R
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the SignIn screen.
 *
 * @param signInViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SignInRoute(
    signInViewModel: SignInViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by signInViewModel.uiState.collectAsState()
    val authResultLauncher =
        rememberLauncherForActivityResult(
            contract = AuthResultContract(),
            onResult = signInViewModel::onGoogleSignInResult,
        )

    val signedIn = uiState.isSignedIn

    LaunchedEffect(uiState.navigateToRegister) {
        if (uiState.navigateToRegister)
            navActions.navigateToRegister()
    }

    if (signedIn)
        LaunchedEffect(Unit) {
            navActions.navigateToMain()
        }
    else
        SignInScreen(
            uiState = uiState,
            onSignInClick = {
                signInViewModel.onSignInClick()
            },
            signInWithGoogle = { authResultLauncher.launch(1) },
            navigateToPhone = { navActions.navigateToPhone() },
            navigateToSignUp = { navActions.navigateToSignUp() },
            onPasswordChanged = signInViewModel::onPasswordChange,
            onEmailChanged = signInViewModel::onEmailChange,
            onPasswordLessChange = signInViewModel::onPasswordlessChange,
        )
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}
