package com.salazar.cheers.ui.main.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

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
    val uiState by searchViewModel.uiState.collectAsState()

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