package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState

/**
 * Stateful composable that displays the Navigation route for the Other profile screen.
 *
 * @param otherProfileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun OtherProfileRoute(
    otherProfileViewModel: OtherProfileViewModel = hiltViewModel(),
    username: String,
    navigateBack: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToOtherProfileStats: () -> Unit,
    navigateToManageFriendship: () -> Unit,
) {
    val uiState by otherProfileViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Toolbar(
                username = username,
                verified = if (uiState is OtherProfileUiState.HasUser) (uiState as OtherProfileUiState.HasUser).user.verified else false,
                onBackPressed = {
                    navigateBack()
                },
                onManageFriendship = {
//                    if (uiState is OtherProfileUiState.HasUser)
//                        navigateToManageFriendship((uiState as OtherProfileUiState.HasUser).user.id)
                },
                onCopyUrl = {
//                    if (uiState is OtherProfileUiState.HasUser)
//                        FirebaseDynamicLinksUtil.createShortLink("u/${(uiState as OtherProfileUiState.HasUser).user.username}")
//                            .addOnSuccessListener { shortLink ->
//                                context.copyToClipboard(shortLink.shortLink.toString())
//                            }
                },
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = otherProfileViewModel::onSwipeRefresh,
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            if (uiState is OtherProfileUiState.HasUser) {
                val uiState = uiState as OtherProfileUiState.HasUser

                OtherProfileScreen(
                    uiState = uiState,
                    onPostClicked = { navigateToPostDetail(it) },
                    onPostLike = otherProfileViewModel::toggleLike,
                    onSendFriendRequest = otherProfileViewModel::sendFriendRequest,
                    onCancelFriendRequest = otherProfileViewModel::cancelFriendRequest,
                    onAcceptFriendRequest = otherProfileViewModel::acceptFriendRequest,
                    onPostMoreClicked = { postId, authorId ->
//                        navigateToPostMoreSheet(postId)
                    },
                    onGiftClick = {
                        val receiverId = uiState.user.id
//                        navigateToSendGift(receiverId)
                    },
                    onStatClicked = { statName, username, verified ->
//                        navigateToOtherProfileStats(
//                            statName,
//                            username,
//                            verified,
//                        )
                    },
                    onMessageClicked = {
//                        navigateToChatWithUserId(uiState.user.id)
                    },
                    onWebsiteClick = { website ->
                        var url = website
                        if (!url.startsWith("www.") && !url.startsWith("http://"))
                            url = "www.$url"
                        if (!url.startsWith("http://"))
                            url = "http://$url"
                        uriHandler.openUri(url)
                    },
                    onStoryClick = { username ->
//                        navigateToStoryWithUserId(username)
                    },
                    onCommentClick = {
                        navigateToComments(it)
                    },
                )
            }
        }
    }
}