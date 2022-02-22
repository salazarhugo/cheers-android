package com.salazar.cheers.ui.otherprofile

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.navigation.CheersNavigationActions
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
        onStatClicked = { statName, username ->
            navActions.navigateToProfileStats(
                statName,
                username
            )
        },
        onPostClicked = { navActions.navigateToPostDetail(it) },
        onMessageClicked = {
            FirestoreChat.getOrCreateChatChannel(uiState.user) { channelId ->
                navActions.navigateToChat(channelId)
            }
        },
        onFollowClicked = { otherProfileViewModel.followUser() },
        onUnfollowClicked = { otherProfileViewModel.unfollowUser() },
        onCopyUrl = {
            val shortLinkTask =
                Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
                    link = Uri.parse("https://cheers-a275e.web.app/${uiState.user.username}")
                    domainUriPrefix = "https://cheers2cheers.page.link"
                    androidParameters { }
                    socialMetaTagParameters {
                        title = "Follow your friend on Cheers!"
                        description = "This link works whether the app is installed or not!"
                    }
                }.addOnSuccessListener { shortLink ->
                    otherProfileViewModel.updateShortLink(shortLink.shortLink.toString())
                }
        },
        onBackPressed = { navActions.navigateBack() },
    )
}

