package com.salazar.cheers.ui.main.chat

import OnMessageLongClickDialog
import android.text.format.DateUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.domain.models.*
import com.salazar.cheers.ui.compose.animations.AnimateHeart
import com.salazar.cheers.ui.compose.chat.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val ConversationTestTag = "ConversationTestTag"

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
                    seen = uiState.channel?.status == RoomStatus.OPENED,
                    isGroup = uiState.channel?.type == RoomType.GROUP,
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
            if (channel?.type == RoomType.DIRECT)
                DirectChatBar(
                    name = channel.name,
                    verified = channel.verified,
                    picture = channel.picture,
                    onNavIconPressed = {
                        onChatUIAction(ChatUIAction.OnBackPressed)
                    },
                    onTitleClick = {
                        onChatUIAction(ChatUIAction.OnUserClick(it))
                    },
                    scrollBehavior = scrollBehavior,
                    onInfoClick = {
                        onChatUIAction(ChatUIAction.OnRoomInfoClick(channel.id))
                    },
                )
            else if (channel?.type == RoomType.GROUP)
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
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.senderId
                val nextAuthor = messages.getOrNull(index + 1)?.senderId
                val prevMessage = messages.getOrNull(index - 1)
                val message = messages[index]
                val isFirstMessageByAuthor = prevAuthor != message.senderId
                val isLastMessageByAuthor = nextAuthor != message.senderId

                if (!DateUtils.isToday(message.createTime * 1000) &&
                    DateUtils.isToday((prevMessage?.createTime ?: 0) * 1000)
                ) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                onChatUIAction(ChatUIAction.OnReplyMessage(message))
                                return@rememberDismissState false
                            }
                            true
                        }
                    )
                    SwipeableMessage(dismissState = dismissState) {
                        Message(
//                        modifier = Modifier.animateItemPlacement(),
                            onAuthorClick = { name -> navigateToProfile(name) },
                            onLongClickMessage = onLongClickMessage,
                            onDoubleTapMessage = onDoubleTapMessage,
                            message = message,
                            isUserMe = message.senderId == FirebaseAuth.getInstance().currentUser?.uid!!,
                            isGroup = isGroup,
                            seen = index == 0 && seen,
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
fun Message(
    modifier: Modifier = Modifier,
    onAuthorClick: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    isUserMe: Boolean,
    isGroup: Boolean,
    seen: Boolean,
    message: ChatMessage,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val spaceBetweenAuthors = if (isLastMessageByAuthor) modifier.padding(top = 8.dp) else modifier
    val horizontalAlignment = if (isUserMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = horizontalAlignment
    ) {
        if (isLastMessageByAuthor && !isUserMe && isGroup) {
            // Avatar
            Image(
                painter = rememberAsyncImagePainter(model = message.senderProfilePictureUrl),
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(message.senderUsername) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            // Space under avatar
//            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            modifier = Modifier.padding(horizontal = 16.dp),
            msg = message,
            isUserMe = isUserMe,
            seen = seen,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            onLongClickMessage = onLongClickMessage,
            onDoubleTapMessage = onDoubleTapMessage,
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: ChatMessage,
    isUserMe: Boolean,
    seen: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isGroup = false
    Column(modifier = modifier) {
        if (isLastMessageByAuthor && !isUserMe && isGroup) {
            AuthorNameTimestamp(msg)
        }
        if (msg.type == MessageType.TEXT)
            ChatItemBubble(
                msg,
                isUserMe = isUserMe,
                seen = seen,
                authorClicked = authorClicked,
                onLongClickMessage = onLongClickMessage,
                onDoubleTapMessage = onDoubleTapMessage
            )
        else if (msg.type == MessageType.IMAGE)
            ImageMessageBubble(
                msg,
                onLongClickMessage = onLongClickMessage,
                onDoubleTapMessage = onDoubleTapMessage,
            )
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AuthorNameTimestamp(msg: ChatMessage) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.senderUsername,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        val formatter = SimpleDateFormat("HH:mm")
        val date = Date(msg.createTime * 1000)

        Text(
            text = formatter.format(date),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleStartShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
private val ChatBubbleEndShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)

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
    // TODO (M3): No Divider, replace when available
    Divider(
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
fun ChatItemBubble(
    message: ChatMessage,
    isUserMe: Boolean,
    seen: Boolean,
    authorClicked: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
) {

    val backgroundBubbleColor = if (isUserMe)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val shape = if (isUserMe) ChatBubbleEndShape else ChatBubbleStartShape

    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = shape,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongClickMessage(message.id) },
                        onDoubleTap = { onDoubleTapMessage(message.id) }
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ClickableMessage(
                    message = message,
                    isUserMe = isUserMe,
                    authorClicked = authorClicked
                )
                TimestampAndStatus(
                    isUserMe = isUserMe,
                    timestamp = message.createTime,
                    status = message.status,
                )
            }
        }
        if (isUserMe && seen)
            Text(
                text = "\uD83D\uDC40",
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground,
            )
        if (message.likedBy.contains(FirebaseAuth.getInstance().currentUser?.uid!!)) {
            AnimateHeart {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.offset(y = (-4).dp)
                ) {
                    Text("❤", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }

//            message.senderProfilePicturePath.let {
//                Spacer(modifier = Modifier.height(4.dp))
//                Surface(
//                    color = backgroundBubbleColor,
//                    shape = ChatBubbleShape
//                ) {
//                    Image(
//                        painter = rememberImagePainter(data = photo.value),
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.size(160.dp),
//                        contentDescription = null
//                    )
//                }
//            }
    }
}

@Composable
fun TimestampAndStatus(
    isUserMe: Boolean,
    timestamp: Long,
    status: ChatMessageStatus,
) {
    val formatter = SimpleDateFormat("HH:mm")
    val date = Date(timestamp * 1000)
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
            MessageStatus(
                modifier = Modifier.size(14.dp),
                status = status,
            )
        }
    }
}

@Composable
fun MessageStatus(
    modifier: Modifier = Modifier,
    status: ChatMessageStatus,
) {
    when (status) {
        ChatMessageStatus.EMPTY -> {}
        ChatMessageStatus.SCHEDULED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        ChatMessageStatus.SENT -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        ChatMessageStatus.DELIVERED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        ChatMessageStatus.READ -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        ChatMessageStatus.FAILED -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Outlined.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        ChatMessageStatus.UNRECOGNIZED -> {}
    }
}

@Composable
fun ClickableMessage(
    message: ChatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val chatMessage =
        if (message.type == MessageType.TEXT)
            message
        else
            null

    val styledMessage = messageFormatter(
        text = chatMessage?.text ?: "Not a text message",
        primary = isUserMe
    )

    val color = if (message.status == ChatMessageStatus.FAILED)
        MaterialTheme.colorScheme.onError
    else if (isUserMe)
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

