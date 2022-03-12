package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
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

    if (uiState.shortLink != null) {
        val localClipboardManager = LocalClipboardManager.current
        localClipboardManager.setText(AnnotatedString(uiState.shortLink!!))
    }

    OtherProfileScreen(
        uiState = uiState,
        onSwipeRefresh = { otherProfileViewModel.refresh() },
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
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onMessageClicked = {
            FirestoreChat.getOrCreateChatChannel(uiState.user.id) { channelId ->
                navActions.navigateToChat(channelId)
            }
        },
        onFollowClicked = { otherProfileViewModel.followUser() },
        onUnfollowClicked = { otherProfileViewModel.unfollowUser() },
        onCopyUrl = {
            FirebaseDynamicLinksUtil.createShortLink(uiState.user.username)
                .addOnSuccessListener { shortLink ->
                    otherProfileViewModel.updateShortLink(shortLink.shortLink.toString())
                }
        },
        onBackPressed = { navActions.navigateBack() },
    )
}

