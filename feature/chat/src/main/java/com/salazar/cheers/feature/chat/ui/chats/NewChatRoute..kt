package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the NewChat screen.
 *
 * @param newChatViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun NewChatRoute(
    newChatViewModel: NewChatViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by newChatViewModel.uiState.collectAsStateWithLifecycle()

    NewChatScreen(
        uiState = uiState,
        onNewGroupClick = newChatViewModel::onNewGroupClick,
        onUserCheckedChange = newChatViewModel::onUserCheckedChange,
        onQueryChange = newChatViewModel::onQueryChange,
        onGroupNameChange = newChatViewModel::onGroupNameChange,
        onBackPressed = { navActions.navigateBack() },
        onFabClick = {
            newChatViewModel.onCreateChat {
                navActions.navigateToChatWithChannelId(it)
            }
        },
    )
}
