package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.MessageType
import com.salazar.cheers.data.chat.models.mockMessage1
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Message(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    isGroup: Boolean = false,
    seen: Boolean = false,
    isFirstMessageByAuthor: Boolean = true,
    isLastMessageByAuthor: Boolean = true,
    onAuthorClick: (String) -> Unit = {},
    onLongClickMessage: (String) -> Unit = {},
    onDoubleTapMessage: (String) -> Unit = {},
) {
    val isSender = message.isSender
    val spaceBetweenAuthors = if (isLastMessageByAuthor) {
        modifier.padding(top = 8.dp)
    } else {
        modifier
    }
    val horizontalAlignment = if (isSender) {
        Arrangement.End
    } else {
        Arrangement.Start
    }

    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = horizontalAlignment
    ) {
        if (isLastMessageByAuthor && !isSender && isGroup) {
            AvatarComponent(
                avatar = message.senderProfilePictureUrl,
                size = 42.dp,
            )
        } else {
            // Space under avatar
//            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            modifier = Modifier.padding(horizontal = 16.dp),
            message = message,
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
    message: ChatMessage,
    seen: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isGroup = false
    val isSender = message.isSender

    Column(modifier = modifier) {
        if (isLastMessageByAuthor && !isSender && isGroup) {
            AuthorNameTimestamp(
                username = message.senderUsername,
                createTime = message.createTime,
            )
        }
        if (message.type == MessageType.TEXT)
            ChatItemBubble(
                message = message,
                seen = seen,
                authorClicked = authorClicked,
                onLongClickMessage = onLongClickMessage,
                onDoubleTapMessage = onDoubleTapMessage
            )
        else if (message.type == MessageType.IMAGE)
            ImageMessageBubble(
                message = message,
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
private fun AuthorNameTimestamp(
    username: String,
    createTime: Long,
) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = username,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        val formatter = SimpleDateFormat("HH:mm")
        val date = Date(createTime)

        Text(
            text = formatter.format(date),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@ComponentPreviews
@Composable
private fun ChatMessagePreview() {
    CheersPreview {
        Message(
            message = mockMessage1,
            modifier = Modifier.padding(16.dp),
        )
    }
}