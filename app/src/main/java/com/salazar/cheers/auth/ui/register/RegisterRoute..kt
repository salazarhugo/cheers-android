package com.salazar.cheers.auth.ui.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.auth.ui.signin.username.ChooseUsernameScreen
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
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
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (!uiState.success) return@LaunchedEffect
        navActions.navigateToMain()
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        modifier = Modifier.systemBarsPadding(),
        state = pagerState,
        userScrollEnabled = false,
    ) { page ->
        Column(modifier = Modifier.fillMaxHeight()) {
            when (page) {
                0 -> WelcomeScreen(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })

                1 -> {
                    ChooseUsernameScreen(username = uiState.username,
                        errorMessage = uiState.errorMessage,
                        isLoading = uiState.isLoading,
                        isUsernameAvailable = uiState.isUsernameAvailable,
                        onClearUsername = registerViewModel::onClearUsername,
                        onUsernameChanged = registerViewModel::onUsernameChanged,
                        onNextClicked = {
                            registerViewModel.checkUsername {
                                if (it) scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            }
                        },
                        onBackPressed = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        })
                }

                2 -> RegisterScreen(uiState = uiState,
                    onRegisterClick = registerViewModel::registerUser,
                    onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
                    onBackPressed = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })
            }
        }
    }
}
