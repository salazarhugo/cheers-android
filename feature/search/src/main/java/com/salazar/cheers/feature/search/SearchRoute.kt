package com.salazar.cheers.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.RecentSearch

/**
 * Stateful composable that displays the Navigation route for the Search screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    navigateToOtherProfile: (String) -> Unit,
    navigateToMap: () -> Unit,
    navigateToParty: (partyID: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onSearchInputChanged = viewModel::onSearchInputChanged,
        onDeleteRecentUser = viewModel::deleteRecentSearch,
        onSwipeRefresh = viewModel::onSwipeRefresh,
        onUserClick = { userItem ->
            viewModel.insertRecentUser(userItem)
            navigateToOtherProfile(userItem.username)
        },
        onRecentSearchClick = {
            when (it) {
                is RecentSearch.Party -> navigateToParty(it.party.id)
                is RecentSearch.Text -> viewModel.onSearchInputChanged(it.text)
                is RecentSearch.User -> navigateToOtherProfile(it.user.username)
            }
        },
        onFollowToggle = viewModel::toggleFollow,
        onMapClick = navigateToMap,
        onBackPressed = onBackPressed,
        onSearch = viewModel::onSearch,
        onPartyClick = navigateToParty,
    )
}