package com.salazar.cheers.ui.sheets.manage_friendship

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

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
    val uiState by viewModel.uiState.collectAsState()

    ManageFriendshipSheet(
        modifier = Modifier.navigationBarsPadding(),
        onManageFriendshipUIAction = { action ->
             when(action) {
                 ManageFriendshipUIAction.OnBlockClick -> viewModel.blockFriend()
                 ManageFriendshipUIAction.OnDoneClick -> navActions.navigateBack()
                 ManageFriendshipUIAction.OnRemoveFriendClick -> viewModel.removeFriend()
                 ManageFriendshipUIAction.OnReportClick -> viewModel.reportFriend()
             }
        },
    )
}