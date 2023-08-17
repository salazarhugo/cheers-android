package com.softimpact.feature.passcode.set

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
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
import com.softimpact.commonlibrary.components.dialogs.PinLockDialog
import com.softimpact.commonlibrary.screens.setpin.SetPinScreen
import kotlinx.coroutines.launch


@Composable
fun SetPinLockRoute(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    setPinLockViewModel: SetPinLockViewModel = hiltViewModel(),
) {
    val uiState by setPinLockViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    SetStatusColors()

    HorizontalPager(
        modifier = Modifier,
        pageCount = 2,
        state = pagerState,
        userScrollEnabled = true,
    ) { page ->
        if (page == 0)
            PinLockDialog(
                onSkip = {
                    navigateBack()
                },
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        else
            SetPinScreen(
                inputPin = uiState.inputPin,
                errorMessage = uiState.errorMessage,
                showSuccess = uiState.success,
                onSubmit = setPinLockViewModel::onSubmit,
                onAddDigit = setPinLockViewModel::onAddDigit,
                onRemoveLastDigit = setPinLockViewModel::onRemoveLastDigit,
                onSkip = {
                    navigateBack()
                },
                onAnimationComplete = {
                    navigateToHome()
                }
            )
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