package com.salazar.cheers.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Search screen.
 *
 * @param searchViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by searchViewModel.uiState.collectAsState()
    SearchScreen(
        uiState = uiState,
        onSearchInputChanged = { searchViewModel.onSearchInputChanged(it) },
        onDeleteRecentUser = { searchViewModel.deleteRecentUser(it) },
        onUserClicked = {
            searchViewModel.insertRecentUser(it)
            navActions.navigateToOtherProfile(it.username)
        },
        onRecentUserClicked = { navActions.navigateToOtherProfile(it) },
    )
}