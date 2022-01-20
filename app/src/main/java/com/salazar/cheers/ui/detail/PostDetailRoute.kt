package com.salazar.cheers.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Post detail screen.
 *
 * @param postDetailViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun PostDetailRoute(
    postDetailViewModel: PostDetailViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by postDetailViewModel.uiState.collectAsState()
    PostDetailScreen(
        uiState = uiState,
        onHeaderClicked = { navActions.navigateToOtherProfile(it) },
        onBackPressed = { navActions.navigateBack() },
    )
}