package com.salazar.cheers.ui.main.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

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
    val uiState by newChatViewModel.uiState.collectAsState()

    NewChatScreen(
        uiState = uiState,
        onNewGroupClick = newChatViewModel::onNewGroupClick,
        onUserCheckedChange = newChatViewModel::onUserCheckedChange,
        onQueryChange = newChatViewModel::onQueryChange,
        onGroupNameChange = newChatViewModel::onGroupNameChange,
        onFabClick = {
            newChatViewModel.onFabClick {
                navActions.navigateToChat(it)
            }
        },
    )
}