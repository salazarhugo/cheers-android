package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateChatRoute(
    newChatViewModel: NewChatViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
) {
    val uiState by newChatViewModel.uiState.collectAsStateWithLifecycle()

    CreateChatScreen(
        uiState = uiState,
        onNewGroupClick = newChatViewModel::onNewGroupClick,
        onUserCheckedChange = newChatViewModel::onUserCheckedChange,
        onQueryChange = newChatViewModel::onQueryChange,
        onGroupNameChange = newChatViewModel::onGroupNameChange,
        onBackPressed = navigateBack,
        onFabClick = {
            newChatViewModel.onCreateChat {
                navigateToChatWithChannelId(it)
            }
        },
    )
}
