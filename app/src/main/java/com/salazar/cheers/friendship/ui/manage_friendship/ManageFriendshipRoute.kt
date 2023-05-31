package com.salazar.cheers.friendship.ui.manage_friendship

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the manage friendship bottom sheet.
 *
 * @param viewModel that handles the business logic of this screen
 */
@Composable
fun ManageFriendshipRoute(
    viewModel: ManageFriendshipViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ManageFriendshipSheet(
        modifier = Modifier.navigationBarsPadding(),
        onManageFriendshipUIAction = { action ->
             when(action) {
                 ManageFriendshipUIAction.OnBlockClick -> viewModel.blockFriend()
                 ManageFriendshipUIAction.OnDoneClick -> navActions.navigateBack()
                 ManageFriendshipUIAction.OnRemoveFriendClick -> {
                     val userId = uiState.userId
                     if (userId == null)
                         navActions.navigateBack()
                     else
                         navActions.navigateToRemoveFriendDialog(userId)
                 }
                 ManageFriendshipUIAction.OnReportClick -> viewModel.reportFriend()
             }
        },
    )
}