package com.salazar.cheers.ui.auth.signin.signup

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.components.share.AppBar
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.auth.signin.CreateAccountScreen
import com.salazar.cheers.ui.auth.signin.username.ChooseUsernameScreen

/**
 * Stateful composable that displays the Navigation route for the SignUp screen.
 *
 * @param signUpViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SignUpRoute(
    signUpViewModel: SignUpViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by signUpViewModel.uiState.collectAsState()

    if (uiState.isSignedIn) {
        navActions.navigateToMain()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Sign up",
                center = true,
                backNavigation = true,
                onNavigateBack = { if (uiState.page > 0) signUpViewModel.prevPage() else navActions.navigateBack() })
        }
    ) {
        when (uiState.page) {
            0 -> ChooseUsernameScreen(
                uiState = uiState,
                onClearUsername = signUpViewModel::onClearUsername,
                onUsernameChanged = signUpViewModel::onUsernameChanged,
                onNextClicked = signUpViewModel::checkUsername,
            )
            1 -> EmailScreen(
                email = uiState.email,
                onEmailChanged = signUpViewModel::onEmailChange,
                onNextClicked = signUpViewModel::verifyEmail,
            )
            2 -> PasswordScreen(
                password = uiState.password,
                onPasswordChanged = signUpViewModel::onPasswordChange,
                onNextClicked = signUpViewModel::verifyPassword,
            )
            3 -> CreateAccountScreen(
                uiState = uiState,
                onPasswordChanged = signUpViewModel::onPasswordChange,
                onEmailChanged = signUpViewModel::onEmailChange,
                onSignUp = signUpViewModel::createAccount,
            )
        }
    }
}

