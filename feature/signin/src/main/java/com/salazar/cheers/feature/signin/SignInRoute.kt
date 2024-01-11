package com.salazar.cheers.feature.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.ErrorMessage
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.CheersDialog
import com.salazar.common.util.LocalActivity
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.StateEventWithContentTriggered

@Composable
fun SignInRoute(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToSignUp: (String?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val signedIn = uiState.isSignedIn
    val event = uiState.dialog

    LaunchedEffect(uiState.navigateToRegister) {
        if (uiState.navigateToRegister)
            navigateToSignUp("email@cheers.social")
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
                onSignInClick = { username ->
                    viewModel.onSignInClick(
                        context = activity,
                        username = username,
                        onSuccess = navigateToHome,
                    )
                },
                navigateToSignUp = {
                    navigateToSignUp(null)
                },
                onUsernameChanged = viewModel::onUsernameChanged,
                onGoogleClick = viewModel::onGoogleButtonClick,
            )
            LaunchedEffect(Unit) {
                viewModel.showSigningOptions(
                    activity
                )
            }
        }

        null -> LoadingScreen()
    }
}

