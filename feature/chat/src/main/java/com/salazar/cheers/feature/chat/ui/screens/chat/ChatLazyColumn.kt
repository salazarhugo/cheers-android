package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.ui.item.party.PlpLoadingIndicatorComponent
import com.salazar.cheers.feature.chat.ui.components.JumpToBottom
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun ChatLazyColumn(
    isLoadingMore: Boolean,
    chatChannel: ChatChannel,
    messages: List<ChatMessage>,
    seen: Boolean,
    isGroup: Boolean,
    navigateToProfile: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    onChatUIAction: (ChatUIAction) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = scrollState,
            reverseLayout = messages.isNotEmpty(),
            modifier = Modifier.fillMaxSize(),
        ) {
            chatMessage(
                messages = messages,
                seen = seen,
                isGroup = isGroup,
                navigateToProfile = navigateToProfile,
                onDoubleTapMessage = onDoubleTapMessage,
                onChatUIAction = onChatUIAction
            )

            if (isLoadingMore) {
                loadingMoreIndicator()
            }

            chatInfos(chatChannel = chatChannel)
        }

        JumpToBottomItem(
            scrollState = scrollState,
        )
    }
}

@Composable
fun BoxScope.JumpToBottomItem(
    scrollState: LazyListState,
) {
    val scope = rememberCoroutineScope()

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

private fun LazyListScope.chatMessage(
    messages: List<ChatMessage>,
    seen: Boolean,
    isGroup: Boolean,
    navigateToProfile: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    onChatUIAction: (ChatUIAction) -> Unit
) {
    itemsIndexed(
        items = messages,
    ) { index, message ->
        val prevAuthor = messages.getOrNull(index - 1)?.senderId
        val nextAuthor = messages.getOrNull(index + 1)?.senderId
        val prevMessage = messages.getOrNull(index - 1)
        val nextMessage = messages.getOrNull(index + 1)
        val isFirstMessageByAuthor = prevAuthor != message.senderId
        val isLastMessageByAuthor = nextAuthor != message.senderId
        val isFirstMessageOfDay = !isSameDate(message.createTime, nextMessage?.createTime)

        if (index >= messages.lastIndex) {
            LaunchedEffect(Unit) {
                onChatUIAction(ChatUIAction.OnLoadMoreChatMessages(messages.lastIndex))
            }
        }

        ChatItem(
            index = index,
            modifier = Modifier.animateItem(),
            chatMessage = message,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            seen = seen,
            isGroup = isGroup,
            onChatUIAction = onChatUIAction,
            navigateToProfile = navigateToProfile,
            onDoubleTapMessage = onDoubleTapMessage,
        )

        if (isFirstMessageOfDay) {
            ChatDayHeaderItem(
                modifier = Modifier.animateItem(),
                messageTime = message.createTime,
            )
        }
    }
}

private fun LazyListScope.loadingMoreIndicator() {
    item("LoadingMoreIndicator") {
        PlpLoadingIndicatorComponent(
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

private fun LazyListScope.chatInfos(
    chatChannel: ChatChannel,
) {
    item {
        ChatDefaultCard(
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .padding(vertical = 64.dp),
            chatChannel = chatChannel,
            onClick = {},
        )
    }
}

private val JumpToBottomThreshold = 56.dp

private fun isSameDate(timestamp: Long, timestamp2: Long?): Boolean {
    if (timestamp2 == null) return false

    val date = Date(timestamp)
    val date2 = Date(timestamp2)
    val res = SimpleDateFormat("MMMM dd").format(date)
    val res2 = SimpleDateFormat("MMMM dd").format(date2)
    return res == res2
}

