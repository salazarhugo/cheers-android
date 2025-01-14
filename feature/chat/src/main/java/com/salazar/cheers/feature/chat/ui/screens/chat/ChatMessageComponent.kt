package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.util.Utils.conditional
import com.salazar.cheers.data.chat.models.mockMessage1
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

enum class SwipeToReplyValue {
    Resting,
    Replying,
}

@Composable
fun ChatMessageComponent(
    chatMessage: ChatMessage,
    modifier: Modifier = Modifier,
    isGroup: Boolean = false,
    isFirstMessageByAuthor: Boolean = true,
    isLastMessageByAuthor: Boolean = true,
    onAuthorClick: (String) -> Unit = {},
    onLongClickMessage: (String) -> Unit = {},
    onDoubleTapMessage: (String) -> Unit = {},
    onReply: () -> Unit,
    onMediaClick: (String) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val isSender = chatMessage.isSender
    val spaceBetweenAuthors = when (isLastMessageByAuthor) {
        true -> modifier.padding(top = 8.dp)
        false -> modifier
    }
    val horizontalAlignment = when (isSender) {
        true -> Arrangement.End
        false -> Arrangement.Start
    }

    val swipeToReplyState = AnchoredDraggableState(
        initialValue = SwipeToReplyValue.Resting,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { with(density) { 100.dp.toPx() } },
        snapAnimationSpec = tween(),
        decayAnimationSpec = exponentialDecay(),
    )

    val anchors = remember(density) {
        val replyOffset = with(density) {
            if (chatMessage.isSender) {
                -48.dp.toPx()
            } else {
                48.dp.toPx()
            }
        }
        DraggableAnchors {
            SwipeToReplyValue.Resting at 0f
            SwipeToReplyValue.Replying at replyOffset
        }
    }
    val messageOverscroll = ScrollableDefaults.overscrollEffect()

    SideEffect {
        swipeToReplyState.updateAnchors(anchors)
    }

    LaunchedEffect(swipeToReplyState) {
        snapshotFlow { swipeToReplyState.settledValue }.collectLatest {
            if (it == SwipeToReplyValue.Replying) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onReply()
                swipeToReplyState.animateTo(SwipeToReplyValue.Resting)
            }
        }
    }

    Row(
        modifier = spaceBetweenAuthors.fillMaxWidth(),
        horizontalArrangement = horizontalAlignment,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isGroup) {
            Spacer(modifier = Modifier.width(8.dp))
            if (isFirstMessageByAuthor && !isSender) {
                AvatarComponent(
                    avatar = chatMessage.senderProfilePictureUrl,
                    name = chatMessage.senderName,
                    username = chatMessage.senderUsername,
                    size = 36.dp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            } else {
                // Space under avatar
                Spacer(modifier = Modifier.width(36.dp))
            }
        }
        AuthorAndTextMessage(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .anchoredDraggable(
                    state = swipeToReplyState,
                    orientation = Orientation.Horizontal,
                    overscrollEffect = messageOverscroll,
                )
                .conditional(chatMessage.isSender.not()) {
                    overscroll(messageOverscroll)
                }
                .offset {
                    IntOffset(
                        x = swipeToReplyState
                            .requireOffset()
                            .roundToInt(),
                        y = 0,
                    )
                },
            message = chatMessage,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            onLongClickMessage = onLongClickMessage,
            onDoubleTapMessage = onDoubleTapMessage,
            onMediaClick = onMediaClick,
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    message: ChatMessage,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    onLongClickMessage: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    onMediaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ChatItemBubble(
            message = message,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = authorClicked,
            onLongClickMessage = onLongClickMessage,
            onDoubleTapMessage = onDoubleTapMessage,
            onMediaClick = onMediaClick,
        )
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@ComponentPreviews
@Composable
private fun ChatMessagePreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        ChatMessageComponent(
            chatMessage = mockMessage1.copy(text = text.take(100)),
            modifier = Modifier.padding(16.dp),
            onReply = {},
            onMediaClick = {},
        )
    }
}