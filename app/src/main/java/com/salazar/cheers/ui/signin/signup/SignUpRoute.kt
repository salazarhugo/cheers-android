package com.salazar.cheers.ui.signin.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.signin.CreateAccountScreen
import com.salazar.cheers.ui.signin.username.ChooseUsernameScreen
import kotlinx.coroutines.launch

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

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    if (uiState.isUsernameAvailable)
        LaunchedEffect(Unit) {
            scope.launch {
                pagerState.animateScrollToPage(1)
            }
        }

    HorizontalPager(
        count = 2,
        state = pagerState,
    ) { page ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (page) {
                0 -> ChooseUsernameScreen(
                    uiState = uiState,
                    onClearUsername = signUpViewModel::onClearUsername,
                    onUsernameChanged = signUpViewModel::onUsernameChanged,
                    onNextClicked = signUpViewModel::checkUsername,
                )
                1 -> CreateAccountScreen(
                    uiState = uiState,
                    onPasswordChanged = signUpViewModel::onPasswordChange,
                    onEmailChanged = signUpViewModel::onEmailChange,
                    onSignUp = signUpViewModel::createAccount,
                )
            }
        }
    }
}

