package com.salazar.cheers.ui.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.salazar.cheers.navigation.CheersNavigationActions

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
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus()

    LaunchedEffect(Unit) {
        homeViewModel.initNativeAdd(context = context)
    }

    HomeScreen(
        uiState = uiState,
        onRefreshPosts = { homeViewModel.refresh() },
        navActions = navActions,
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onPostMoreClicked = { a, b -> navActions.navigateToPostMoreSheet(a, b) },
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        navigateToAddEvent = { navActions.navigateToAddEvent() },
        navigateToAddPost = { navActions.navigateToAddPostSheet() },
        onSelectTab = homeViewModel::selectTab,
        onLike = homeViewModel::toggleLike,
        navigateToComments = { navActions.navigateToPostComments(it.post.id) },
        navigateToSearch = { navActions.navigateToSearch() },
        onEventClicked = { navActions.navigateToEventDetail(it) },
        onStoryClick = { navActions.navigateToStory() },
    )
}