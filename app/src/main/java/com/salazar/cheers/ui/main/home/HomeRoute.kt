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
        navActions = navActions,
        onSwipeRefresh = homeViewModel::onSwipeRefresh,
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onPostMoreClicked = { postId, authorId ->
            navActions.navigateToPostMoreSheet(
                postId,
                authorId
            )
        },
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        navigateToAddEvent = { navActions.navigateToAddEvent() },
        navigateToAddPost = { navActions.navigateToAddPostSheet() },
        onSelectTab = {
            if (it == 1)
                navActions.navigateToEvents()
        },
        onLike = homeViewModel::toggleLike,
        navigateToComments = { navActions.navigateToPostComments(it.id) },
        navigateToSearch = { navActions.navigateToSearch() },
        onEventClicked = { navActions.navigateToEventDetail(it) },
        onStoryClick = { navActions.navigateToStory() },
        onAddStoryClick = { navActions.navigateToCamera() },
        onActivityClick = { navActions.navigateToActivity() }
    )
}