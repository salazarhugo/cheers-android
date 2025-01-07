package com.salazar.cheers.feature.chat.ui.screens.chat

import android.content.ClipDescription
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.data.chat.models.mockMessage1
import com.salazar.cheers.feature.chat.ui.components.ChatBottomBar
import com.salazar.cheers.feature.chat.ui.components.ChatPresenceIndicator
import kotlinx.coroutines.launch


@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onChatUIAction: (ChatUIAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()
    val channel = uiState.channel ?: return
    val chatMessages = uiState.messages
    val dragAndDropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                return true
            }
        }
    }
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            ChatTopBar(
                chatChannel = channel,
                onChatUIAction = onChatUIAction,
                scrollBehavior = scrollBehavior,
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the ChatBottomBar composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    },
                    target = dragAndDropCallback,
                )
        ) {
            ChatLazyColumn(
                chatChannel = channel,
                messages = chatMessages,
                isLoadingMore = uiState.isLoadingMore,
                seen = uiState.channel.status == ChatStatus.OPENED,
                isGroup = uiState.channel.type == ChatType.GROUP,
                navigateToProfile = { onChatUIAction(ChatUIAction.OnUserClick(it)) },
                modifier = Modifier.weight(1f),
                scrollState = scrollState,
                onDoubleTapMessage = { onChatUIAction(ChatUIAction.OnLikeClick(it)) },
                onChatUIAction = {
                    when (it) {
                        is ChatUIAction.OnReplyMessage -> {
                            scope.launch {
                                inputFocusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        }

                        else -> Unit
                    }
                    onChatUIAction(it)
                },
            )
            ChatPresenceIndicator(
                isPresent = uiState.channel.isOtherUserPresent,
                modifier = Modifier.padding(16.dp),
            )
            ChatBottomBar(
                // let this element handle the padding so that the elevation is shown behind the
                // navigation bar
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
                inputFocusRequester = inputFocusRequester,
                textState = uiState.textState,
                replyMessage = uiState.replyMessage,
                onMessageSent = {
                    onChatUIAction(ChatUIAction.OnSendTextMessage(it))
                },
                onTextChanged = {
                    onChatUIAction(ChatUIAction.OnTextInputChange(it))
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                onImageSelectorClick = {
                    onChatUIAction(ChatUIAction.OnImageSelectorClick)
                },
                onChatUIAction = onChatUIAction,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun ChatScreenPreview() {
    CheersPreview {
        ChatScreen(
            uiState = ChatUiState(
                isLoading = false,
                channel = ChatChannel(
                    name = "Cheers Official",
                    membersCount = 9,
                    type = ChatType.GROUP,
                    verified = true,
                ),
                messages = listOf(
                    mockMessage1,
                    mockMessage1,
                    mockMessage1,
                )
            ),
            onChatUIAction = {},
        )
    }
}