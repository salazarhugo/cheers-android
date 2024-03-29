package com.salazar.cheers.feature.chat.ui.screens.chat

import OnMessageLongClickDialog
import android.text.format.DateUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.SymbolAnnotationType
import com.salazar.cheers.core.ui.messageFormatter
import com.salazar.cheers.data.chat.models.ChatMessage
import com.salazar.cheers.data.chat.models.ChatMessageStatus
import com.salazar.cheers.data.chat.models.ChatStatus
import com.salazar.cheers.data.chat.models.ChatType
import com.salazar.cheers.feature.chat.ui.components.ChatBottomBar
import com.salazar.cheers.feature.chat.ui.components.ChatPresenceIndicator
import com.salazar.cheers.feature.chat.ui.components.DirectChatBar
import com.salazar.cheers.feature.chat.ui.components.GroupChatBar
import com.salazar.cheers.feature.chat.ui.components.JumpToBottom
import com.salazar.cheers.feature.chat.ui.components.SwipeableMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onChatUIAction: (ChatUIAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val selectedMessage = remember { mutableStateOf<ChatMessage?>(null) }
    val channel = uiState.channel

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Messages(
                    seen = uiState.channel?.status == ChatStatus.OPENED,
                    isGroup = uiState.channel?.type == ChatType.GROUP,
                    messages = uiState.messages,
                    navigateToProfile = {
                        onChatUIAction(ChatUIAction.OnUserClick(it))
                    },
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    onLongClickMessage = {
                        openDialog.value = true
                    },
                    onDoubleTapMessage = {
                        onChatUIAction(ChatUIAction.OnLikeClick(it))
                    },
                    onChatUIAction = onChatUIAction,
                )
                ChatPresenceIndicator(
                    isPresent = uiState.channel?.isOtherUserPresent ?: false,
                    modifier = Modifier.padding(16.dp),
                )
                ChatBottomBar(
                    modifier = Modifier.navigationBarsPadding(),
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
            when (channel?.type) {
                ChatType.DIRECT -> {
                    DirectChatBar(
                        name = channel.name,
                        verified = channel.verified,
                        picture = channel.picture,
                        isTyping = channel.isOtherUserTyping,
                        onNavIconPressed = {
                            onChatUIAction(ChatUIAction.OnBackPressed)
                        },
                        onTitleClick = {
                            onChatUIAction(ChatUIAction.OnUserClick(channel.otherUserId))
                        },
                        scrollBehavior = scrollBehavior,
                        onInfoClick = {
                            onChatUIAction(ChatUIAction.OnRoomInfoClick(channel.id))
                        },
                    )
                }

                ChatType.GROUP -> {
                    GroupChatBar(
                        name = channel.name,
                        members = channel.members.size,
                        picture = channel.picture,
                        onNavIconPressed = {
                            onChatUIAction(ChatUIAction.OnBackPressed)
                        },
                        onTitleClick = {},
                        onInfoClick = {
                            onChatUIAction(ChatUIAction.OnRoomInfoClick(channel.id))
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }

                else -> {}
            }
        }
    }

    val a = selectedMessage.value

    if (openDialog.value && a != null)
        OnMessageLongClickDialog(
            openDialog = openDialog,
            msg = a,
            onUnsendMessage = {
                onChatUIAction(ChatUIAction.OnUnSendMessage(it))
            },
            onCopyText = {
                onChatUIAction(ChatUIAction.OnCopyText(it))
            },
            onLike = {
                onChatUIAction(ChatUIAction.OnLikeClick(it))
            },
            onUnlike = {
                onChatUIAction(ChatUIAction.OnUnLikeClick(it))
            },
        )
}

@Composable
fun Messages(
    messages: List<ChatMessage>,
    seen: Boolean,
    isGroup: Boolean,
    navigateToProfile: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    onChatUIAction: (ChatUIAction) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding = PaddingValues(top = 56.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.senderId
                val nextAuthor = messages.getOrNull(index + 1)?.senderId
                val prevMessage = messages.getOrNull(index - 1)
                val message = messages[index]
                val isFirstMessageByAuthor = prevAuthor != message.senderId
                val isLastMessageByAuthor = nextAuthor != message.senderId

                if (!DateUtils.isToday(message.createTime) &&
                    DateUtils.isToday((prevMessage?.createTime ?: 0))
                ) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                onChatUIAction(ChatUIAction.OnReplyMessage(message))
                                return@rememberSwipeToDismissBoxState false
                            }
                            true
                        }
                    )
                    SwipeableMessage(state = dismissState) {
                        Message(
                            message = message,
                            isGroup = isGroup,
                            seen = index == 0 && seen,
                            onAuthorClick = { name -> navigateToProfile(name) },
                            onLongClickMessage = onLongClickMessage,
                            onDoubleTapMessage = onDoubleTapMessage,
                            isFirstMessageByAuthor = isFirstMessageByAuthor,
                            isLastMessageByAuthor = isLastMessageByAuthor,
                        )
                    }
                }
            }
        }

        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun ImageMessageBubble(
    message: ChatMessage,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
) {
    Column {
//        message.imagesDownloadUrl.forEach { downloadUrl ->
        var tap by remember { mutableStateOf(false) }
        AsyncImage(
            model = message.photoUrl,
            contentDescription = null,
            modifier = Modifier
                .let { if (tap) it.fillMaxSize() else it.aspectRatio(4 / 5f) }
                .animateContentSize()
                .padding(3.dp)
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongClickMessage(message.id) },
                        onDoubleTap = { onDoubleTapMessage(message.id) },
                        onTap = { tap = !tap }
                    )
                },
            contentScale = ContentScale.Crop,
        )
    }
}


@Composable
fun TimestampAndStatus(
    isUserMe: Boolean,
    timestamp: Long,
    status: ChatMessageStatus,
) {
    val formatter = SimpleDateFormat("HH:mm")
    val date = Date(timestamp)
    val color = if (isUserMe)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .padding(bottom = 8.dp, end = 12.dp, top = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = formatter.format(date),
            style = MaterialTheme.typography.bodySmall,
            color = color,
        )

        if (isUserMe) {
            ChatMessageStatus(
                modifier = Modifier.size(14.dp),
                status = status,
            )
        }
    }
}

@Composable
fun ClickableMessage(
    text: String?,
    isSender: Boolean,
    status: ChatMessageStatus,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = text ?: "Not a text message",
        primary = isSender,
    )

    val color = if (status == ChatMessageStatus.FAILED)
        MaterialTheme.colorScheme.onError
    else if (isSender)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = color),
        modifier = Modifier.padding(vertical = 10.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollState.atBottom(): Boolean = value == 0
//}

