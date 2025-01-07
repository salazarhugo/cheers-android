package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.SymbolAnnotationType
import com.salazar.cheers.core.ui.animations.AnimateHeart
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.messageFormatter
import com.salazar.cheers.data.chat.models.mockMessage1
import java.text.SimpleDateFormat
import java.util.Date

private val ChatBubbleStartShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
private val ChatBubbleEndShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)

@Composable
fun ChatItemBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    seen: Boolean = false,
    isFirstMessageByAuthor: Boolean = true,
    isLastMessageByAuthor: Boolean = true,
    authorClicked: (String) -> Unit = {},
    onLongClickMessage: (String) -> Unit = {},
    onDoubleTapMessage: (String) -> Unit = {},
) {
    val isSender = message.isSender
    val replyToChatMessage = message.replyTo
    val backgroundBubbleColor = when (isSender) {
        true -> MaterialTheme.colorScheme.primary
        false -> MaterialTheme.colorScheme.surfaceVariant
    }
    val replyBubbleColor = when (isSender) {
        true -> MaterialTheme.colorScheme.onPrimary
        false -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val topEnd = when (isLastMessageByAuthor) {
        true -> 20.dp
        false -> 4.dp
    }
    val bottomEnd = when (isFirstMessageByAuthor) {
        true -> 20.dp
        false -> 4.dp
    }
    val shape = when (isSender) {
        true -> RoundedCornerShape(
            topStart = 20.dp,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = 20.dp,
        )

        false -> RoundedCornerShape(
            topStart = topEnd,
            topEnd = 20.dp,
            bottomEnd = 20.dp,
            bottomStart = bottomEnd,
        )
    }

    Column(
        modifier = modifier,
    ) {
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
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (replyToChatMessage != null) {
                    ReplyChatMessage(
                        containerColor = replyBubbleColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        message = replyToChatMessage,
                    )
                }
                if (message.images.isNotEmpty()) {
                    ChatImageComponent(
                        message = message,
                        onLongClickMessage = {},
                        onDoubleTapMessage = {},
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ClickableMessage(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(bottom = 8.dp),
                        text = message.text,
                        isSender = isSender,
                        status = message.status,
                        authorClicked = authorClicked
                    )
                    TimestampAndStatus(
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 12.dp),
                        isUserMe = isSender,
                        timestamp = message.createTime,
                        status = message.status,
                    )
                }
            }
        }
        if (message.hasLiked) {
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
    }
}

@Composable
fun ClickableMessage(
    text: String?,
    isSender: Boolean,
    status: ChatMessageStatus,
    modifier: Modifier,
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
        modifier = modifier,
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

@Composable
fun TimestampAndStatus(
    isUserMe: Boolean,
    timestamp: Long,
    status: ChatMessageStatus,
    modifier: Modifier = Modifier,
) {
    val formatter = SimpleDateFormat("HH:mm")
    val date = Date(timestamp)
    val color = if (isUserMe)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
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

@ComponentPreviews
@Composable
private fun ChatItemBubblePreview2(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        ChatItemBubble(
            modifier = Modifier.padding(16.dp),
            message = mockMessage1.copy(
                text = text.take(10),
                replyTo = mockMessage1.copy(senderName = "Cheers")
            ),
        )
    }
}

@ComponentPreviews
@Composable
private fun ChatItemBubblePreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        ChatItemBubble(
            modifier = Modifier.padding(16.dp),
            message = mockMessage1.copy(text = text.take(100)),
        )
    }
}

