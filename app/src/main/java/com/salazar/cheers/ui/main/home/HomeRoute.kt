package com.salazar.cheers.ui.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
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
        onSwipeRefresh = homeViewModel::onSwipeRefresh,
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onPostMoreClicked = { postId, authorId ->
            navActions.navigateToPostMoreSheet(
                postId,
                authorId
            )
        },
        onUserClicked = { navActions.navigateToOtherProfile(it) },
        onLike = homeViewModel::toggleLike,
        navigateToComments = { navActions.navigateToPostComments(it.id) },
        navigateToSearch = { navActions.navigateToSearch() },
        onStoryClick = { navActions.navigateToStoryWithUserId(it) },
        onAddStoryClick = { navActions.navigateToCamera() },
        onActivityClick = { navActions.navigateToActivity() },
        onCommentClick = { navActions.navigateToPostComments(it) },
        onAddPostClick = { navActions.navigateToAddPostSheet() },
    )
}