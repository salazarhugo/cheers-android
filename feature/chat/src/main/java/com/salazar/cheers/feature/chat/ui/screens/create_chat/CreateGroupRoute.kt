package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateGroupRoute(
    newGroupViewModel: NewChatViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToGroupWithChannelId: (String) -> Unit,
) {
    val uiState by newGroupViewModel.uiState.collectAsStateWithLifecycle()

    CreateGroupScreen(
        uiState = uiState,
        onNewGroupClick = newGroupViewModel::onNewGroupClick,
        onUserCheckedChange = newGroupViewModel::onUserCheckedChange,
        onQueryChange = newGroupViewModel::onQueryChange,
        onGroupNameChange = newGroupViewModel::onGroupNameChange,
        onBackPressed = navigateBack,
        onFabClick = {
        },
    )
}
