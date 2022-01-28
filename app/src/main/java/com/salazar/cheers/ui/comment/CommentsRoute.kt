package com.salazar.cheers.ui.comment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Comments screen.
 *
 * @param commentsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CommentsRoute(
    commentsViewModel: CommentsViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by commentsViewModel.uiState.collectAsState()

    CommentsScreen(
        uiState = uiState,
    )
}