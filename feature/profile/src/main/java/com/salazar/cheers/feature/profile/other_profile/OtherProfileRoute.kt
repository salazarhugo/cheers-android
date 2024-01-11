package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.data.user.User

@Composable
fun OtherProfileRoute(
    viewModel: OtherProfileViewModel = hiltViewModel(),
    username: String,
    navigateBack: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToOtherProfileStats: (User) -> Unit,
    navigateToManageFriendship: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    when(uiState) {
        is OtherProfileUiState.NoUser -> LoadingScreen()
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
                        OtherProfileUIAction.OnSendMessageClick -> TODO()
                        OtherProfileUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                        OtherProfileUIAction.OnGiftClick -> TODO()
                        OtherProfileUIAction.OnFriendshipClick -> navigateToManageFriendship(user.id)
                    }
                },
            )
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
