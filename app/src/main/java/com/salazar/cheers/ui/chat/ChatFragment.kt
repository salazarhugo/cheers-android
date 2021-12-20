package com.salazar.cheers.ui.chat

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.ChannelNameBar
import com.salazar.cheers.components.JumpToBottom
import com.salazar.cheers.components.UserInput
import com.salazar.cheers.internal.Message
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.TextMessage
import com.salazar.cheers.util.StorageUtil
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val ConversationTestTag = "ConversationTestTag"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    channelId: String,
    messages: List<Message>,
    onMessageSent: (channelId: String, msg: String) -> Unit,
    onPoBackStack: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    val scope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize())
        {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Messages(
                    messages = messages,
                    navigateToProfile = {},
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                )
                UserInput(
                    onMessageSent = {
                        onMessageSent(channelId, it)
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    modifier = Modifier.navigationBarsWithImePadding(),
                )
            }
            ChannelNameBar(
                channelName = channelId,
                channelMembers = 2,
                onNavIconPressed = { onPoBackStack() },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.statusBarsPadding(),
            )
        }

    }
}

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
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
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.senderId
                val isLastMessageByAuthor = nextAuthor != content.senderId

                // Hardcode day dividers for simplicity
                if (index == messages.size - 1) {
                    item {
                        DayHeader("20 Aug")
                    }
                } else if (index == 2) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
//                    AnimateMessage {
                    Message(
                        onAuthorClick = { name -> navigateToProfile(name) },
                        message = content,
                        isUserMe = content.senderId == FirebaseAuth.getInstance().currentUser?.uid!!,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    isUserMe: Boolean,
    message: Message,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            // Avatar
            val photo = remember { mutableStateOf<Uri?>(null) }

            if (message.senderProfilePicturePath.isNotBlank())
                StorageUtil.pathToReference(message.senderProfilePicturePath)?.downloadUrl?.addOnSuccessListener {
                    photo.value = it
                }
            Image(
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(message.senderId) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                painter = rememberImagePainter(
                    data = message.senderProfilePicturePath,
                ),
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
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor && !isUserMe) {
            AuthorNameTimestamp(msg)
        }
        ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
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

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

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
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor = if (isUserMe)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant


    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }

//            message.senderProfilePicturePath.let {
//                Spacer(modifier = Modifier.height(4.dp))
//                Surface(
//                    color = backgroundBubbleColor,
//                    shape = ChatBubbleShape
//                ) {
//                    val photo = remember { mutableStateOf<Uri?>(null) }
//
//                    if (message.senderProfilePicturePath.isNotBlank())
//                        StorageUtil.pathToReference(message.senderProfilePicturePath)?.downloadUrl?.addOnSuccessListener {
//                            photo.value = it
//                        }
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

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = color),
        modifier = Modifier.padding(12.dp),
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

