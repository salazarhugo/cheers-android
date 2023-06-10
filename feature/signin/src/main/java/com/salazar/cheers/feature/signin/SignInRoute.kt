package com.salazar.cheers.feature.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.ErrorMessage
import com.salazar.cheers.core.ui.CheersDialog
import com.salazar.cheers.data.auth.AuthResultContract
import com.salazar.cheers.data.auth.GoogleOneTapSignInButton
import com.salazar.cheers.data.auth.getIdToken
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.StateEventWithContentTriggered

/**
 * Stateful composable that displays the Navigation route for the SignIn screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SignInRoute(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToPhone: () -> Unit,
    navigateToSignUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authResultLauncher =
        rememberLauncherForActivityResult(
            contract = AuthResultContract(),
            onResult = {
                val idToken = getIdToken(it)
                viewModel.onGoogleSignInResult(idToken)
            },
        )

    val signedIn = uiState.isSignedIn
    val event = uiState.dialog

    LaunchedEffect(uiState.navigateToRegister) {
        if (uiState.navigateToRegister)
            navigateToRegister()
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

    when(signedIn) {
        true -> {
            LaunchedEffect(Unit) {
                navigateToHome()
            }
        }
        false -> {
            SignInScreen(
                uiState = uiState,
                onSignInClick = viewModel::onSignInClick,
                signInWithGoogle = { authResultLauncher.launch(1) },
                navigateToPhone = {
                    navigateToPhone()
                },
                navigateToSignUp = {
                    navigateToSignUp()
                },
                onPasswordChanged = viewModel::onPasswordChange,
                onEmailChanged = viewModel::onEmailChange,
                onPasswordLessChange = viewModel::onPasswordlessChange,
            )
            GoogleOneTapSignInButton(
                onSignIn = viewModel::signInWithOneTap,
            )
        }

        null -> com.salazar.cheers.core.share.ui.LoadingScreen()
    }
}

