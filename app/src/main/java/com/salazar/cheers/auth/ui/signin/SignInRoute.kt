package com.salazar.cheers.auth.ui.signin

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.salazar.cheers.R
import com.salazar.cheers.core.domain.model.ErrorMessage
import com.salazar.cheers.core.ui.CheersDialog
import com.salazar.cheers.navigation.CheersNavigationActions
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.StateEventWithContentTriggered

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
    val event = uiState.dialog

    LaunchedEffect(uiState.navigateToRegister) {
        if (uiState.navigateToRegister)
            navActions.navigateToRegister()
    }

    var open by remember { mutableStateOf(false) }

    EventEffect(
        event = event,
        onConsumed = {},
        action = {
            open = true
        },
    )

    if (event is StateEventWithContentTriggered<ErrorMessage>) {
        CheersDialog(
            error = event.content,
            openDialog = open,
            onDismiss = {
                open = false
            },
        )
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
