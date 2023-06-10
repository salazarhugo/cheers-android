package com.salazar.cheers.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Stateful composable that displays the Navigation route for the Search screen.
 *
 * @param searchViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SearchRoute(
    searchViewModel: SearchViewModel = hiltViewModel(),
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onSearchInputChanged = { searchViewModel.onSearchInputChanged(it) },
        onDeleteRecentUser = { searchViewModel.deleteRecentUser(it) },
        onSwipeRefresh = searchViewModel::onSwipeRefresh,
        onUserClicked = {
            searchViewModel.insertRecentUser(it)
            navigateToOtherProfile(it)
        },
        onRecentUserClicked = {
            navigateToOtherProfile(it)
        },
        onFollowToggle = searchViewModel::toggleFollow,
    )
}