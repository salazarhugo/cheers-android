package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.util.FirebaseDynamicLinksUtil

/**
 * Stateful composable that displays the Navigation route for the Other profile screen.
 *
 * @param otherProfileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun OtherProfileRoute(
    otherProfileViewModel: OtherProfileViewModel,
    navActions: CheersNavigationActions,
    username: String,
) {
    val uiState by otherProfileViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    if (uiState.shortLink != null) {
        val localClipboardManager = LocalClipboardManager.current
        localClipboardManager.setText(AnnotatedString(uiState.shortLink!!))
    }

    Scaffold(
        topBar = {
            Toolbar(
                username = username,
                verified = if (uiState is OtherProfileUiState.HasUser) (uiState as OtherProfileUiState.HasUser).user.verified else false,
                onBackPressed = { navActions.navigateBack() },
                onCopyUrl = {
                    if (uiState is OtherProfileUiState.HasUser)
                        FirebaseDynamicLinksUtil.createShortLink((uiState as OtherProfileUiState.HasUser).user.username)
                            .addOnSuccessListener { shortLink ->
                                otherProfileViewModel.updateShortLink(shortLink.shortLink.toString())
                            }
                },
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = otherProfileViewModel::refresh,
            modifier = Modifier.padding(it),
        ) {
            if (uiState is OtherProfileUiState.HasUser) {
                val uiState = uiState as OtherProfileUiState.HasUser

                OtherProfileScreen(
                    uiState = uiState,
                    onPostClicked = { navActions.navigateToPostDetail(it) },
                    onPostLike = otherProfileViewModel::toggleLike,
                    onFollowToggle = otherProfileViewModel::toggleFollow,
                    onPostMoreClicked = { postId, authorId ->
                        navActions.navigateToPostMoreSheet(
                            postId,
                            authorId
                        )
                    },
                    onGiftClick = {
                        val receiverId = uiState.user.id
                        navActions.navigateToSendGift(receiverId)
                    },
                    onStatClicked = { statName, username, verified ->
                        navActions.navigateToOtherProfileStats(
                            statName,
                            username,
                            verified,
                        )
                    },
                    onMessageClicked = {
                        otherProfileViewModel.getRoomId { roomId ->
                            navActions.navigateToChat(roomId)
                        }
                    },
                    onWebsiteClick = { website ->
                        var url = website
                        if (!url.startsWith("www.") && !url.startsWith("http://"))
                            url = "www.$url"
                        if (!url.startsWith("http://"))
                            url = "http://$url"
                        uriHandler.openUri(url)
                    }
                )
            }
        }
    }
}