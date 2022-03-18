package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.util.FirestoreChat

/**
 * Stateful composable that displays the Navigation route for the Other profile screen.
 *
 * @param otherProfileViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun OtherProfileRoute(
    otherProfileViewModel: OtherProfileViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by otherProfileViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    if (uiState.shortLink != null) {
        val localClipboardManager = LocalClipboardManager.current
        localClipboardManager.setText(AnnotatedString(uiState.shortLink!!))
    }

    if (uiState is OtherProfileUiState.HasUser) {
        val uiState = uiState as OtherProfileUiState.HasUser

        OtherProfileScreen(
            uiState = uiState,
            onBackPressed = { navActions.navigateBack() },
            onSwipeRefresh = { otherProfileViewModel.refresh() },
            onPostClicked = { navActions.navigateToPostDetail(it) },
            onFollowClicked = { otherProfileViewModel.followUser() },
            onUnfollowClicked = { otherProfileViewModel.unfollowUser() },
            onGiftClick = {
                val receiverId = uiState.user.id
                navActions.navigateToSendGift(receiverId)
            },
            onStatClicked = { statName, username ->
                navActions.navigateToProfileStats(
                    statName,
                    username
                )
            },
            onMessageClicked = {
                FirestoreChat.getOrCreateChatChannel(uiState.user.id) { channelId ->
                    navActions.navigateToChat(channelId)
                }
            },
            onCopyUrl = {
                FirebaseDynamicLinksUtil.createShortLink(uiState.user.username)
                    .addOnSuccessListener { shortLink ->
                        otherProfileViewModel.updateShortLink(shortLink.shortLink.toString())
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

