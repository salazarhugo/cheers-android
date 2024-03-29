package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.data.user.User
import kotlinx.coroutines.launch

@Composable
fun OtherProfileRoute(
    viewModel: OtherProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToOtherProfileStats: (User) -> Unit,
    navigateToManageFriendship: (String) -> Unit,
    navigateToChat: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = rememberRefreshLayoutState()
    val scope  = rememberCoroutineScope()

    LaunchedEffect(uiState.isRefreshing) {
        if (!uiState.isRefreshing) {
            scope.launch {
                state.finishRefresh(true)
            }
        }
    }

    Scaffold(
        topBar = {
            OtherProfileTopBar(
                username = uiState.username,
                verified = (uiState as? OtherProfileUiState.HasUser)?.user?.verified ?: false,
                onBackPressed = navigateBack,
                onCopyUrl = {},
                onManageFriendship = { navigateToManageFriendship(uiState.username) },
            )
        }
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = viewModel::onSwipeRefresh,
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            when(uiState) {
                is OtherProfileUiState.Loading -> LoadingScreen()
                is OtherProfileUiState.NotFound -> {
                    UserNotFoundMessage(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is OtherProfileUiState.HasUser -> {
                    val user = (uiState as OtherProfileUiState.HasUser).user
                    OtherProfileScreen(
                        uiState = uiState as OtherProfileUiState.HasUser,
                        onOtherProfileUIAction = { action ->
                            when(action) {
                                is OtherProfileUIAction.OnSendFriendRequest -> viewModel.sendFriendRequest(action.userID)
                                is OtherProfileUIAction.OnAcceptFriendRequest -> viewModel.acceptFriendRequest(action.userID)
                                is OtherProfileUIAction.OnCancelFriendRequest -> viewModel.cancelFriendRequest(action.userID)
                                is OtherProfileUIAction.OnPostClick -> navigateToPostDetail(action.postID)
                                OtherProfileUIAction.OnBackPressed -> navigateBack()
                                OtherProfileUIAction.OnEditProfileClick -> TODO()
                                OtherProfileUIAction.OnFriendListClick -> navigateToOtherProfileStats(user)
                                OtherProfileUIAction.OnSendMessageClick -> navigateToChat(user.id)
                                OtherProfileUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                                OtherProfileUIAction.OnGiftClick -> TODO()
                                OtherProfileUIAction.OnFriendshipClick -> navigateToManageFriendship(user.id)
                            }
                        },
                    )
                }
            }
        }
    }
}

//                onPostLike = viewModel::toggleLike,
//                onWebsiteClick = { website ->
//                    var url = website
//                    if (!url.startsWith("www.") && !url.startsWith("http://"))
//                        url = "www.$url"
//                    if (!url.startsWith("http://"))
//                        url = "http://$url"
//                    uriHandler.openUri(url)
//                },
//                onStoryClick = { username -> },
//                onCommentClick = {
//                    navigateToComments(it)
//                },
