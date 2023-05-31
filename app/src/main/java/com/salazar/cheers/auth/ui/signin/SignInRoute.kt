package com.salazar.cheers.auth.ui.signin

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.salazar.cheers.R
import com.salazar.cheers.core.domain.model.ErrorMessage
import com.salazar.cheers.core.ui.CheersDialog
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import com.salazar.cheers.core.share.ui.LoadingScreen
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
    navActions: CheersNavigationActions,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authResultLauncher =
        rememberLauncherForActivityResult(
            contract = AuthResultContract(),
            onResult = viewModel::onGoogleSignInResult,
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

    when(signedIn) {
        true -> {
            LaunchedEffect(Unit) {
                navActions.navigateToMain()
            }
        }
        false -> {
            SignInScreen(
                uiState = uiState,
                onSignInClick = viewModel::onSignInClick,
                signInWithGoogle = { authResultLauncher.launch(1) },
                navigateToPhone = { navActions.navigateToPhone() },
                navigateToSignUp = { navActions.navigateToSignUp() },
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

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}

@Composable
fun GoogleOneTapSignInButton(onSignIn: (String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            try {
                val credential = Identity.getSignInClient(context).getSignInCredentialFromIntent(it.data)
                val idtoken = credential.googleIdToken
                val username = credential.id
                val password = credential.password
                when {
                    idtoken != null -> {
                        // got an id token from google. use it to authenticate
                        // with your backend.
                        Log.d("Auth", "got id token.")
                        onSignIn(idtoken)
                    }
                    password != null -> {
                        // got a saved username and password. use them to authenticate
                        // with your backend.
                        Log.d("Auth", "got password.")
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.d("Auth", "No ID token or password!")
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        },
    )

    LaunchedEffect(Unit) {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()

        Identity.getSignInClient(context).beginSignIn(request)
            .addOnSuccessListener {
                val intentSenderRequest = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                try {
                    launcher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .addOnFailureListener { e ->
                Log.d("Google Identity", e.localizedMessage)
            }
    }
}