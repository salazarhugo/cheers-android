package com.salazar.cheers.ui.main.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.internal.User
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Chat screen.
 *
 * @param chatViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                chatViewModel.sendImageMessage(listOf(it))
        }

    when (uiState) {
        is ChatUiState.HasChannel -> {
            val ui = (uiState as ChatUiState.HasChannel)
            val otherUser =
                ui.channel.members.firstOrNull { it.id != FirebaseAuth.getInstance().currentUser?.uid!! }
                    ?: User()

            LaunchedEffect(Unit) {
                chatViewModel.seenLastMessage()
            }

            ChatScreen(
                uiState = uiState as ChatUiState.HasChannel,
                onTitleClick = { navActions.navigateToOtherProfile(it) },
                onPoBackStack = { navActions.navigateBack() },
                onUnlike = chatViewModel::unlikeMessage,
                onLike = chatViewModel::likeMessage,
                onUnsendMessage = chatViewModel::unsendMessage,
                onMessageSent = chatViewModel::sendTextMessage,
                onImageSelectorClick = { launcher.launch("image/*") },
                onCopyText = {},
                username = otherUser.username,
                verified = otherUser.verified,
                name = otherUser.fullName,
                profilePicturePath = otherUser.profilePictureUrl,
                onAuthorClick = { navActions.navigateToOtherProfile(it) },
            )
        }
        is ChatUiState.NoChannel -> {
            LoadingScreen()
        }
    }
}