package com.salazar.cheers.ui.main.story.feed

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
import com.salazar.cheers.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState
import com.salazar.cheers.ui.main.story.StoryScreen
import com.salazar.cheers.ui.main.story.StoryUIAction
import com.salazar.cheers.ui.main.story.StoryViewModel
import kotlinx.coroutines.launch

/**
 * Stateful composable that displays the Navigation route for the StoryFeed screen.
 *
 * @param storyFeedViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryFeedRoute(
    appState: CheersAppState,
    storyFeedViewModel: StoryFeedViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by storyFeedViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val errorMessage = uiState.errorMessage

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

    SetStoryStatusBars()

    StoryFeedScreen(
        uiState = uiState,
        onStoryFeedUIAction = { action ->
            when (action) {
                is StoryFeedUIAction.OnBackPressed -> navActions.navigateBack()
                is StoryFeedUIAction.OnDelete -> {}
                is StoryFeedUIAction.OnActivity -> {}
                is StoryFeedUIAction.OnViewed -> storyFeedViewModel.onViewed(action.storyId)
                is StoryFeedUIAction.OnToggleLike -> storyFeedViewModel.onToggleLike(action.storyId, action.liked)
                is StoryFeedUIAction.OnMoreClick -> navActions.navigateToStoryMoreSheet(action.storyId)
                else -> {}
            }
        },
    )
}

@Composable
fun SetStoryStatusBars() {
    val lifecycleOwner = LocalLifecycleOwner.current
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
}
