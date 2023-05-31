package com.salazar.cheers.ui.main.story.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the StoryStatsRoute screen.
 *
 * @param storyStatsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun StoryStatsRoute(
    storyStatsViewModel: StoryStatsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by storyStatsViewModel.uiState.collectAsStateWithLifecycle()

    StoryStatsScreen(
        uiState = uiState,
        onUserClick = { navActions.navigateToOtherProfile(it) },
        onDeleteStory = storyStatsViewModel::onDeleteStory,
        onCloseClick = { navActions.navigateBack() },
    )
}
