package com.salazar.cheers.ui.main.chat

import OnMessageLongClickDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Surface
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
import com.salazar.cheers.MessageType
import com.salazar.cheers.RoomStatus
import com.salazar.cheers.RoomType
import com.salazar.cheers.compose.UserInput
import com.salazar.cheers.compose.animations.AnimateHeart
import com.salazar.cheers.compose.chat.DirectChatBar
import com.salazar.cheers.compose.chat.GroupChatBar
import com.salazar.cheers.compose.chat.JumpToBottom
import com.salazar.cheers.compose.chat.SendingChat
import com.salazar.cheers.internal.ChatMessage
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
    onAuthorClick: (String) -> Unit,
    onImageSelectorClick: () -> Unit,
    onPoBackStack: () -> Unit,
    onTextChanged: () -> Unit,
    onInfoClick: () -> Unit,
    micInteractionSource: MutableInteractionSource,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val selectedMessage = remember { mutableStateOf<ChatMessage?>(null) }
    val channel = uiState.channel
    val sending = uiState.channel.status == RoomStatus.SENDING

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Messages(
                    seen = uiState.channel.status == RoomStatus.OPENED,
                    sending = sending,
                    messages = uiState.messages,
                    navigateToProfile = onAuthorClick,
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
                    onTextChanged = onTextChanged,
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    modifier = Modifier.imePadding(),
                    onImageSelectorClick = onImageSelectorClick,
                    micInteractionSource = micInteractionSource,
                )
            }
            if (channel.type == RoomType.DIRECT)
                DirectChatBar(
                    name = name,
                    username = username,
                    verified = verified,
                    profilePictureUrl = profilePicturePath,
                    onNavIconPressed = { onPoBackStack() },
                    onTitleClick = onTitleClick,
                    scrollBehavior = scrollBehavior,
                    onInfoClick = onInfoClick,
                )
            else if (channel.type == RoomType.GROUP)
                GroupChatBar(
                    name = channel.name,
                    members = channel.members.size,
                    profilePictureUrl = profilePicturePath,
                    onNavIconPressed = { onPoBackStack() },
                    onTitleClick = {},
                    onInfoClick = onInfoClick,
                    scrollBehavior = scrollBehavior,
                )
        }
    }

    val a = selectedMessage.value

    if (openDialog.value && a != null)
        OnMessageLongClickDialog(
            openDialog,
            msg = a,
            onUnsendMessage = onUnsendMessage,
            onCopyText = onCopyText,
            onLike = onLike,
            onUnlike = onUnlike,
        )
}

@Composable
fun Messages(
    messages: List<ChatMessage>,
    seen: Boolean,
    sending: Boolean,
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
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
//                insets = LocalWindowInsets.current.statusBars,
//                additionalTop = 56.dp
//            ),
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
                if (!message.time.isToday() && prevMessage?.time?.isToday() == true) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    Message(
//                        modifier = Modifier.animateItemPlacement(),
                        onAuthorClick = { name -> navigateToProfile(name) },
                        onLongClickMessage = onLongClickMessage,
                        onDoubleTapMessage = onDoubleTapMessage,
                        message = message,
                        isUserMe = message.senderId == FirebaseAuth.getInstance().currentUser?.uid!!,
                        seen = index == 0 && seen,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                    )
                }
            }
            if (sending)
                item {
                    SendingChat()
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
        if (isLastMessageByAuthor && !isUserMe) {
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
            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            msg = message,
            isUserMe = isUserMe,
            seen = seen,
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
    Column(modifier = modifier) {
        if (isLastMessageByAuthor && !isUserMe) {
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
        val date = Date(msg.time.seconds * 1000)

        Text(
            text = formatter.format(date),
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

    val color = if (!message.acknowledged)
        MaterialTheme.colorScheme.onError
    else if (isUserMe)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = color),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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

