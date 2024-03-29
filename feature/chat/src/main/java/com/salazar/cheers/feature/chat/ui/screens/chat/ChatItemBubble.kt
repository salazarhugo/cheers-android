package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.AnimateHeart
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.data.chat.models.ChatMessage
import com.salazar.cheers.data.chat.models.mockMessage1

private val ChatBubbleStartShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
private val ChatBubbleEndShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)

@Composable
fun ChatItemBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    seen: Boolean = false,
    authorClicked: (String) -> Unit = {},
    onLongClickMessage: (String) -> Unit = {},
    onDoubleTapMessage: (String) -> Unit = {},
) {
    val isSender = message.isSender

    val backgroundBubbleColor = when (isSender) {
        true -> MaterialTheme.colorScheme.primary
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val shape = when (isSender) {
        true -> ChatBubbleEndShape
        false -> ChatBubbleStartShape
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
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ClickableMessage(
                    text = message.text,
                    isSender = isSender,
                    status = message.status,
                    authorClicked = authorClicked
                )
                TimestampAndStatus(
                    isUserMe = isSender,
                    timestamp = message.createTime,
                    status = message.status,
                )
            }
        }
        if (isSender && seen) {
            Text(
                text = "\uD83D\uDC40",
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground,
            )
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

@ComponentPreviews
@Composable
private fun ChatItemBubblePreview() {
    CheersPreview {
        ChatItemBubble(
            modifier = Modifier.padding(16.dp),
            message = mockMessage1,
        )
    }
}
