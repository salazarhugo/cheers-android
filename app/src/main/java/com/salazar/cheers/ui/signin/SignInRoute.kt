package com.salazar.cheers.ui.signin

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    signInViewModel: SignInViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by signInViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account != null)
                    signInViewModel.firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
            }
        }
    SignInScreen(
        uiState = uiState,
        signInWithEmailPassword = {
            signInViewModel.signInWithEmailPassword()
            keyboardController?.hide()
        },
        navigateToPhone = { navActions.navigateToPhone() },
        navigateToSignUp = { navActions.navigateToSignUp() },
        onPasswordChanged = signInViewModel::onPasswordChange,
        onEmailChanged = signInViewModel::onEmailChange,
        signInWithGoogle = { authResultLauncher.launch(1) },
    )
    if (uiState.isSignedIn) {
        navActions.navigateToMain()
    }
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}
