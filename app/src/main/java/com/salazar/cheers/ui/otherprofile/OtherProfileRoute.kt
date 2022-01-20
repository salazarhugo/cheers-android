package com.salazar.cheers.ui.otherprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions
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
    OtherProfileScreen(
        uiState = uiState,
        onSwipeRefresh = { otherProfileViewModel.refresh() },
        onStatClicked = { statName, username ->  navActions.navigateToProfileStats(statName, username) },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onMessageClicked = {
                FirestoreChat.getOrCreateChatChannel(uiState.user) { channelId ->
                    navActions.navigateToChat(
                        channelId,
                        uiState.user.username,
                        uiState.user.verified,
                        uiState.user.fullName,
                        uiState.user.profilePictureUrl,
                    )
                }
        },
        onFollowClicked = { otherProfileViewModel.followUser() },
        onUnfollowClicked = { otherProfileViewModel.unfollowUser() },
        onCopyUrl = {},
        onBackPressed = {},
    )
}

