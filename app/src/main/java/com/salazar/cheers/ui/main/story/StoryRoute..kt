package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
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
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val uiState by storyViewModel.uiState.collectAsStateWithLifecycle()
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
                        uiState.sheetState.show()
                    }
                }

                else -> {}
            }
        },
        onStoryOpen = storyViewModel::onStorySeen,
        onNavigateBack = { navActions.navigateBack() },
        onUserClick = { navActions.navigateToOtherProfile(it) },
        onInputChange = storyViewModel::onInputChange,
        onSendReaction = storyViewModel::onSendReaction,
        showInterstitialAd = {},
        onPauseChange = storyViewModel::onPauseChange,
        onCurrentStepChange = storyViewModel::onCurrentStepChange,
    )
}
