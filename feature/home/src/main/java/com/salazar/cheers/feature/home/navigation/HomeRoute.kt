package com.salazar.cheers.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onActivityClick: () -> Unit,
    onPostClick: (String) -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessage
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus()

    LaunchedEffect(Unit) {
        homeViewModel.initNativeAdd(context = context)
    }

//    if (errorMessage != null) {
//        LaunchedEffect(appState.snackBarHostState) {
//            appState.showSnackBar(errorMessage)
//        }
//    }

    HomeScreen(
        uiState = uiState,
        onHomeUIAction = { action ->
            when (action) {
                is HomeUIAction.OnActivityClick -> onActivityClick()
                is HomeUIAction.OnLikeClick -> homeViewModel.toggleLike(action.post)
                is HomeUIAction.OnCommentClick -> {}
                is HomeUIAction.OnSearchClick -> {}
                is HomeUIAction.OnStoryClick -> {}
                is HomeUIAction.OnStoryFeedClick -> {}
                is HomeUIAction.OnUserClick -> {}
                is HomeUIAction.OnPostClick -> onPostClick(action.postID)
                is HomeUIAction.OnSwipeRefresh -> homeViewModel.onSwipeRefresh()
                is HomeUIAction.OnCreatePostClick -> {}//navActions.navigateToCreatePost()
                is HomeUIAction.OnAddStoryClick -> {}//navActions.navigateToCamera()
                is HomeUIAction.OnPostMoreClick -> {}// navActions.navigateToPostMoreSheet(action.postID)
                is HomeUIAction.OnLoadNextItems -> homeViewModel.loadNextPosts()
                is HomeUIAction.OnChatClick -> {}// navActions.navigateToMessages()
                is HomeUIAction.OnShareClick -> {}// navActions.navigateToShare(action.postID)
                HomeUIAction.OnCreateNoteClick -> {}// navActions.navigateToCreateNote()
                is HomeUIAction.OnNoteClick -> {}// navActions.navigateToNote(action.userID)
                is HomeUIAction.OnAddFriendClick -> homeViewModel.onAddFriendClick(action.userID)
            }
        }
    )
}