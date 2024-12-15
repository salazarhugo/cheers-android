package com.salazar.cheers.feature.home.home

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.Settings
import com.salazar.cheers.core.ui.ui.RequestPermission

@Composable
fun HomeRoute(
    appSettings: Settings,
    viewModel: HomeViewModel = hiltViewModel(),
    onActivityClick: () -> Unit,
    onPostClick: (String) -> Unit,
    navigateToNote: (String) -> Unit,
    navigateToCreatePost: () -> Unit,
    navigateToCreateNote: () -> Unit,
    navigateToCreateParty: () -> Unit,
    navigateToUser: (String) -> Unit,
    navigateToParties: () -> Unit,
    navigateToMessages: () -> Unit,
    navigateToPostMoreSheet: (String) -> Unit,
    navigateToPostComments: (String) -> Unit,
    navigateToPostLikes: (String) -> Unit = {},
    navigateToSignIn: () -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToMap: () -> Unit = {},
    navigateToDeletePostDialog: (String) -> Unit,
    navigateToPartyDetail: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessage
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    focusManager.clearFocus()

    LaunchedEffect(Unit) {
        viewModel.initNativeAdd(context = context)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestPermission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
        )
    }
    RequestPermission(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onGranted = viewModel::onLocationPermissionGranted,
    )
    HomeScreen(
        uiState = uiState,
        initialSelectedTab = appSettings.selectedHomeTab,
        navigateToPartyDetail = navigateToPartyDetail,
        navigateToCreateParty = navigateToCreateParty,
        onHomeUIAction = { action ->
            when (action) {
                is HomeUIAction.OnActivityClick -> onActivityClick()
                is HomeUIAction.OnLikeClick -> viewModel.toggleLike(action.post)
                is HomeUIAction.OnPostCommentClick -> navigateToPostComments(action.postID)
                is HomeUIAction.OnPostLikesClick -> navigateToPostLikes(action.postID)
                is HomeUIAction.OnSearchClick -> navigateToParties() //navigateToCamera()
                is HomeUIAction.OnStoryClick -> {}
                is HomeUIAction.OnStoryFeedClick -> {}
                is HomeUIAction.OnUserClick -> navigateToUser(action.userID)
                is HomeUIAction.OnPostClick -> onPostClick(action.postID)
                is HomeUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                is HomeUIAction.OnCreatePostClick -> {
                    if (uiState.account == null)
                        navigateToSignIn()
                    else
                        navigateToCreatePost()
                }

                is HomeUIAction.OnAddStoryClick -> {}//navActions.navigateToCamera()
                is HomeUIAction.OnLoadNextItems -> viewModel.loadNextPosts()
                is HomeUIAction.OnChatClick -> navigateToMessages()
                is HomeUIAction.OnShareClick -> {}// navActions.navigateToShare(action.postID)
                HomeUIAction.OnCreateNoteClick -> navigateToCreateNote()
                is HomeUIAction.OnNoteClick -> navigateToNote(action.userID)
                is HomeUIAction.OnAddFriendClick -> viewModel.onAddFriendClick(action.userID)
                HomeUIAction.OnPartiesClick -> navigateToParties()
                is HomeUIAction.OnAudioClick -> viewModel.onAudioClick(
                    action.postID,
                    action.audioUrl
                )

                is HomeUIAction.OnDeletePostClick -> navigateToDeletePostDialog(action.postID)
                is HomeUIAction.OnSelectPage -> viewModel.updatePage(action.page)
                HomeUIAction.OnMapClick -> navigateToMap()
            }
        }
    )
}