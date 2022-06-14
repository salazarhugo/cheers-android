package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.navigation.CheersNavigationActions

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
) {
    val uiState by storyViewModel.uiState.collectAsState()
    val systemUiController = rememberSystemUiController()
    val darkIcons = !isSystemInDarkTheme()

    DisposableEffect(lifecycleOwner) {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setSystemBarsColor(
                if (darkIcons) Color.White else Color.Black,
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
