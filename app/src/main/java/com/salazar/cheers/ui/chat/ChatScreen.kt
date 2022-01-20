package com.salazar.cheers.ui.chat

import OnMessageLongClickDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.AnimateHeart
import com.salazar.cheers.components.DirectChatBar
import com.salazar.cheers.components.JumpToBottom
import com.salazar.cheers.components.UserInput
import com.salazar.cheers.internal.ImageMessage
import com.salazar.cheers.internal.Message
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.TextMessage
import com.salazar.cheers.util.Utils.isToday
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun ChatScreen(
    uiState: ChatUiState.HasChannel,
    name: String,
    username: String,
    verified: Boolean,
    profilePicturePath: String,
    onMessageSent: (msg: String) -> Unit,
    onUnsendMessage: (msgId: String) -> Unit,
    onLike: (msgId: String) -> Unit,
    onUnlike: (msgId: String) -> Unit,
    onTitleClick: (username: String) -> Unit,
    onCopyText: (String) -> Unit,
    onImageSelectorClick: () -> Unit,
    onPoBackStack: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val selectedMessage = remember { mutableStateOf<Message?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Messages(
                    uiState = uiState,
                    messages = uiState.messages,
                    navigateToProfile = {},
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    onLongClickMessage = {
                        openDialog.value = true
//                        selectedMessage.value = it
                    },
                    onDoubleTapMessage = onLike
                )
                UserInput(
                    onMessageSent = {
                        onMessageSent(it)
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    modifier = Modifier.imePadding(),
                    onImageSelectorClick = onImageSelectorClick,
                )
            }
            DirectChatBar(
                name = name,
                username = username,
                verified = verified,
                profilePictureUrl = profilePicturePath,
                onNavIconPressed = { onPoBackStack() },
                onTitleClick = onTitleClick,
                scrollBehavior = scrollBehavior,
//                modifier = Modifier.statusBarsPadding(),
            )
        }
    }

    if (openDialog.value)
        OnMessageLongClickDialog(
            openDialog,
            msg = selectedMessage.value ?: TextMessage(),
            onUnsendMessage = onUnsendMessage,
            onCopyText = onCopyText,
            onLike = onLike,
            onUnlike = onUnlike,
        )
}

@Composable
fun Messages(
    uiState: ChatUiState.HasChannel,
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.statusBars,
                additionalTop = 56.dp
            ),
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

                // Hardcode day dividers for simplicity
                if (message.time?.isToday() == false && prevMessage?.time?.isToday() == true) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
//                    AnimateMessage {
                    Message(
                        uiState = uiState,
                        onAuthorClick = { name -> navigateToProfile(name) },
                        onLongClickMessage = onLongClickMessage,
                        onDoubleTapMessage = onDoubleTapMessage,
                        message = message,
                        isUserMe = message.senderId == FirebaseAuth.getInstance().currentUser?.uid!!,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                    )
//                    }
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
    uiState: ChatUiState.HasChannel,
    onAuthorClick: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    isUserMe: Boolean,
    message: Message,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    val horizontalAlignment = if (isUserMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = horizontalAlignment
    ) {
        if (isLastMessageByAuthor && !isUserMe) {
            // Avatar
            Image(
                painter = rememberImagePainter(
                    data = message.senderProfilePictureUrl,
                ),
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(message.senderId) })
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
            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            uiState = uiState,
            msg = message,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            onLongClickMessage = onLongClickMessage,
            onDoubleTapMessage = onDoubleTapMessage,
            modifier = Modifier
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    uiState: ChatUiState.HasChannel,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor && !isUserMe && false) {
            AuthorNameTimestamp(msg)
        }
        if (msg is TextMessage)
            ChatItemBubble(
                uiState = uiState,
                msg,
                isUserMe,
                authorClicked = authorClicked,
                onLongClickMessage = onLongClickMessage,
                onDoubleTapMessage = onDoubleTapMessage
            )
        else if (msg is ImageMessage)
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
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.senderUsername,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        val formatter = SimpleDateFormat("HH:mm")
        Text(
            text = formatter.format(msg.time ?: Date(0)),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleStartShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleEndShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)

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
    message: ImageMessage,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
) {
    Column {
        message.imagesDownloadUrl.forEach { downloadUrl ->
            Image(
                painter = rememberImagePainter(data = downloadUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClickMessage(message.id) },
                            onDoubleTap = { onDoubleTapMessage(message.id) }
                        )
                    },
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItemBubble(
    uiState: ChatUiState.HasChannel,
    message: Message,
    isUserMe: Boolean,
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
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClickMessage(message.id) },
                    onDoubleTap = { onDoubleTapMessage(message.id) }
                )
            }
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }

        if (isUserMe && message.id == uiState.channel.recentMessage.id &&
            uiState.channel.recentMessage.seenBy.containsAll(uiState.channel.members)
        ) {
            Text("Seen", color = MaterialTheme.colorScheme.onBackground)
        }
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
fun ClickableMessage(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val textMessage =
        if (message.type == MessageType.TEXT)
            message as TextMessage
        else
            null

    val styledMessage = messageFormatter(
        text = textMessage?.text ?: "Not a text message",
        primary = isUserMe
    )

    val color = if (isUserMe) MaterialTheme.colorScheme.onPrimary else
        MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = color),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
    )
//    ClickableText(
//        text = styledMessage,
//        style = MaterialTheme.typography.bodyLarge.copy(color = color),
//        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
//        onClick = {
//            styledMessage
//                .getStringAnnotations(start = it, end = it)
//                .firstOrNull()
//                ?.let { annotation ->
//                    when (annotation.tag) {
//                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
//                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
//                        else -> Unit
//                    }
//                }
//        }
//    )
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollState.atBottom(): Boolean = value == 0
//}

