package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.sheets.SuccessSplashView
import kotlinx.coroutines.delay

/**
 * Stateful composable that displays the Navigation route for the Story screen.
 *
 * @param storyViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryRoute(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    storyViewModel: StoryViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
    showInterstitialAd: () -> Unit,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val uiState by storyViewModel.uiState.collectAsState()
    val systemUiController = rememberSystemUiController()
    val darkIcons = !isSystemInDarkTheme()

    val background = MaterialTheme.colorScheme.background

    DisposableEffect(lifecycleOwner) {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setSystemBarsColor(
                if (darkIcons) Color.White else background,
                darkIcons = darkIcons
            )
        }
    }


    StoryScreen(
        uiState = uiState,
        onStoryUIAction = { action, storyId ->
            when (action) {
                is StoryUIAction.OnDelete -> storyViewModel.onDelete(storyId = storyId)
                is StoryUIAction.OnActivity -> navActions.navigateToStoryStats(storyId)
            }
        },
        onStoryOpen = storyViewModel::onStoryOpen,
        onNavigateBack = { navActions.navigateBack() },
        onUserClick = { navActions.navigateToOtherProfile(it) },
        value = uiState.input,
        onInputChange = storyViewModel::onInputChange,
        onSendReaction = storyViewModel::onSendReaction,
        onFocusChange = storyViewModel::onPauseChange,
        showInterstitialAd = showInterstitialAd,
    )
}
