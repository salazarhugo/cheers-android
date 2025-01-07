package com.salazar.cheers.feature.chat.ui.screens.chat

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.NoScreenshot
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.Permission


@Composable
fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel(),
    showSnackBar: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToRoomDetails: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                viewModel.updateImages(listOf(it))
            }
        }

    DisposableEffect(Unit) {
        viewModel.startPresence()
        viewModel.sendReadReceipt()
        onDispose {
            viewModel.endPresence()
        }
    }

    if (!uiState.errorMessage.isNullOrBlank()) {
        LaunchedEffect(Unit) {
            showSnackBar(uiState.errorMessage!!)
        }
    }

    val micInteractionSource = remember { MutableInteractionSource() }
    val isPressed by micInteractionSource.collectIsPressedAsState()

    if (isPressed) {
        Permission(permission = Manifest.permission.RECORD_AUDIO) {
            Permission(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                SideEffect {
                    viewModel.startRecording()
                }
                DisposableEffect(Unit) {
                    onDispose {
                        viewModel.stopRecording()
                    }
                }
            }
        }
    }

    NoScreenshot()

    val channel = uiState.channel
    if (channel == null)
        LoadingScreen()
    else {
        ChatScreen(
            uiState = uiState,
            onChatUIAction = { action ->
                when (action) {
                    is ChatUIAction.OnReplyMessage -> viewModel.onReplyMessage(action.message)
                    is ChatUIAction.OnLikeClick -> viewModel.likeMessage(action.messageId)
                    is ChatUIAction.OnUnLikeClick -> viewModel.unlikeMessage(action.messageId)
                    is ChatUIAction.OnSwipeRefresh -> TODO()
                    is ChatUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                    is ChatUIAction.OnBackPressed -> navigateBack()
                    is ChatUIAction.OnUnSendMessage -> viewModel.deleteChatMessage(action.messageId)
                    is ChatUIAction.OnSendTextMessage -> viewModel.sendChatMessage(action.text)
                    is ChatUIAction.OnImageSelectorClick -> launcher.launch("image/*")
                    is ChatUIAction.OnCopyText -> TODO()
                    is ChatUIAction.OnTextInputChange -> viewModel.onTextChanged(action.text)
                    is ChatUIAction.OnRoomInfoClick -> navigateToRoomDetails(action.roomId)
                    is ChatUIAction.OnLoadMoreChatMessages -> viewModel.onLoadMore(action.index)
                }
            },
        )
    }
}