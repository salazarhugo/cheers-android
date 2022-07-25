package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.navigation.CheersNavigationActions
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

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
                is StoryUIAction.OnMore -> {
                    scope.launch {
                        uiState.sheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
            }
        },
        onStoryOpen = storyViewModel::onStorySeen,
        onNavigateBack = { navActions.navigateBack() },
        onUserClick = { navActions.navigateToOtherProfile(it) },
        onInputChange = storyViewModel::onInputChange,
        onSendReaction = storyViewModel::onSendReaction,
        showInterstitialAd = showInterstitialAd,
        onPauseChange = storyViewModel::onPauseChange,
        onCurrentStepChange = storyViewModel::onCurrentStepChange,
    )
}
