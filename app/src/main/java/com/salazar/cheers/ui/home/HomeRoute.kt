package com.salazar.cheers.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onRefreshPosts = { homeViewModel.refresh() },
        navActions = navActions,
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        navigateToAddEvent = {},
        navigateToAddPost = { navActions.navigateToAddPostSheet() },
        onSelectTab = homeViewModel::selectTab,
    )
}