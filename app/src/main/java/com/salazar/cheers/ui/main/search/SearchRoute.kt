package com.salazar.cheers.ui.main.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Search screen.
 *
 * @param searchViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onSearchInputChanged = { searchViewModel.onSearchInputChanged(it) },
        onDeleteRecentUser = { searchViewModel.deleteRecentUser(it) },
        onSwipeRefresh = searchViewModel::onSwipeRefresh,
        onUserClicked = {
            searchViewModel.insertRecentUser(it)
            navActions.navigateToOtherProfile(it)
        },
        onRecentUserClicked = { navActions.navigateToOtherProfile(it) },
        onFollowToggle = searchViewModel::toggleFollow,
    )
}