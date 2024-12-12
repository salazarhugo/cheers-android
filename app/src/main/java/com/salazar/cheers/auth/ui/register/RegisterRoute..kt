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
import kotlinx.coroutines.launch

@Composable
fun RegisterRoute(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
) {
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (!uiState.success) return@LaunchedEffect
        navigateToHome()
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
                0 -> WelcomeScreen(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                )

                1 -> {}

                2 -> RegisterScreen(
                    uiState = uiState,
                    onRegisterClick = registerViewModel::registerUser,
                    onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
                    onBackPressed = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                )
            }
        }
    }
}
