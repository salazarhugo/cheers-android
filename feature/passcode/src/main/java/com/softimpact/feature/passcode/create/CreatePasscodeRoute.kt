package com.softimpact.feature.passcode.create

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch


@Composable
fun CreatePasscodeRoute(
    navigateBack: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
    createPasscodeViewModel: CreatePasscodeViewModel = hiltViewModel(),
) {
    val uiState by createPasscodeViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        2
    }

    Scaffold() {
        HorizontalPager(
            modifier = Modifier.padding(it),
            state = pagerState,
            userScrollEnabled = true,
        ) { page ->
            when (page) {
                0 -> PasscodeOnBoarding(
                    onSkip = {
                        navigateBack()
                    },
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                else -> CreatePasscodeScreen(
                    inputPin = uiState.inputPin,
                    onSubmit = {
                        createPasscodeViewModel.onSubmit(
                            onSuccess = navigateToPasscodeSettings,
                        )
                    },
                    onAddDigit = createPasscodeViewModel::onAddDigit,
                    onRemoveLastDigit = createPasscodeViewModel::onRemoveLastDigit,
                    onSkip = {
                        navigateBack()
                    },
                )
            }
        }
    }
}

@Composable
fun SetStatusColors() {
    val systemUiController = rememberSystemUiController()
    val background = MaterialTheme.colorScheme.background
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setNavigationBarColor(
                color = background,
                darkIcons = false,
            )
        }
    }
}