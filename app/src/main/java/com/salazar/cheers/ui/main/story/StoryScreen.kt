package com.salazar.cheers.ui.main.story

import androidx.compose.runtime.Composable
import com.salazar.cheers.data.db.entities.Story


@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    onStoryOpen: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUserClick: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
    showInterstitialAd: () -> Unit,
    onPauseChange: (Boolean) -> Unit,
    onCurrentStepChange: (Int) -> Unit,
) {
}
