package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserItem

@Composable
fun CreateChatRoute(
    newChatViewModel: NewChatViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToChat: (UserItem) -> Unit,
) {
    val uiState by newChatViewModel.uiState.collectAsStateWithLifecycle()

    CreateChatScreen(
        uiState = uiState,
        onNewGroupClick = newChatViewModel::onNewGroupClick,
        onQueryChange = newChatViewModel::onQueryChange,
        onBackPressed = navigateBack,
        navigateToChat = navigateToChat,
    )
}
