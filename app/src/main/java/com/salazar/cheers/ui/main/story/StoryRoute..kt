package com.salazar.cheers.ui.main.story

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Story screen.
 *
 * @param storyViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryRoute(
    storyViewModel: StoryViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by storyViewModel.uiState.collectAsState()

    StoryScreen(
        uiState = uiState,
        onStoryClick = {},
        onStoryOpen = storyViewModel::onStoryOpen,
        onNavigateBack = { navActions.navigateBack() }
    )
}
