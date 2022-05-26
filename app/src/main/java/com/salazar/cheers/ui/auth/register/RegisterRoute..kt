package com.salazar.cheers.ui.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.auth.signin.username.ChooseUsernameScreen
import kotlinx.coroutines.launch

/**
 * Stateful composable that displays the Navigation route for the Register screen.
 *
 * @param registerViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun RegisterRoute(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by registerViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (!uiState.success) return@LaunchedEffect
        navActions.navigateToMain()
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.isUsernameAvailable) {
        if (!uiState.isUsernameAvailable) return@LaunchedEffect
        scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage+1)
        }
    }

    HorizontalPager(
        count = 2,
        state = pagerState,
        userScrollEnabled = false,
    ) { page ->

        Column(modifier = Modifier.fillMaxHeight()) {
            when (page) {
                0 ->
                    ChooseUsernameScreen(
                        username = uiState.username,
                        errorMessage = uiState.errorMessage,
                        isLoading = uiState.isLoading,
                        isUsernameAvailable = uiState.isUsernameAvailable,
                        onClearUsername = registerViewModel::onClearUsername,
                        onUsernameChanged = registerViewModel::onUsernameChanged,
                        onNextClicked = {
                            registerViewModel.checkUsername()
                        },
                    )
                1 ->
                    RegisterScreen(
                        uiState = uiState,
                        onRegisterClick = registerViewModel::register,
                        onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
                    )
            }
        }
    }

}
