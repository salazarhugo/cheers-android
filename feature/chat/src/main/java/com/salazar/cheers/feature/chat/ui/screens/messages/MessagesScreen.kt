package com.salazar.cheers.feature.chat.ui.screens.messages

import RoomsUIAction
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.components.message.MessageComponent
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.feature.chat.ui.chats.EmptyChatsMessage
import com.salazar.cheers.feature.chat.ui.components.SwipeableChatItem
import com.salazar.cheers.feature.chat.ui.components.chat_item.DirectChatComponent


@Composable
fun MessagesScreen(
    uiState: MessagesUiState,
    onNewChatClicked: () -> Unit,
    onRoomsUIAction: (RoomsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            MessagesTopBar(
                websocketState = uiState.websocketState,
                onNewChatClicked = onNewChatClicked,
                onBackPressed = {
                    onRoomsUIAction(RoomsUIAction.OnBackPressed)
                },
            )
        },
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = { onRoomsUIAction(RoomsUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(it),
        ) {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                Tabs(
                    uiState = uiState,
                    onRoomsUIAction = onRoomsUIAction,
                )
            }
        }
    }
}

@Composable
fun Tabs(
    uiState: MessagesUiState,
    onRoomsUIAction: (RoomsUIAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        if (uiState.isLoading) {
            LoadingScreen()
        }

        CheersSearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            searchInput = uiState.searchInput,
            onSearchInputChanged = { onRoomsUIAction(RoomsUIAction.OnSearchInputChange(it)) },
            placeholder = {
                Text("Search")
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
        )

        ConversationList(
            channels = uiState.channels,
            onRoomsUIAction = onRoomsUIAction,
        )
    }
}

@Composable
fun ConversationList(
    channels: List<ChatChannel>?,
    onRoomsUIAction: (RoomsUIAction) -> Unit,
) {
    if (channels == null)
        return

    if (channels.isEmpty()) {
        EmptyChatsMessage()
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        items(
            items = channels,
            key = { it.id },
        ) { channel ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onRoomsUIAction(RoomsUIAction.OnPinRoom(channel.id))
                        return@rememberSwipeToDismissBoxState false
                    }
                    true
                }
            )

            SwipeableChatItem(
                modifier = Modifier
                    .animateItem()
                    .clip(RoundedCornerShape(8.dp)),
                state = dismissState,
            ) {
                DirectChatComponent(
                    channel = channel,
                    onRoomsUIAction = onRoomsUIAction,
                )
            }
        }
    }
}

@Composable
fun LinkContactsItem() {
    Row {
        Button(onClick = { /*TODO*/ }) { }
    }
}


@Composable
fun GroupConversation(
    channel: ChatChannel,
    onChannelClicked: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChannelClicked(channel.id) }
            .padding(15.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            val image = channel.recentMessage.senderProfilePictureUrl

//            Image(
//                painter = rememberImagePainter(
//                    data = image,
//                    builder = {
//                        transformations(CircleCropTransformation())
//                        error(R.drawable.default_group_picture)
//                    },
//                ),
//                contentDescription = "Profile image",
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(Modifier.width(8.dp))
//            Column {
//                val title = channel.name
//                val subtitle =
//                    "${channel.recentMessage.senderUsername}: ${channel.recentMessage.text}"
//                Text(text = title, style = Typography.bodyMedium)
//                Text(
//                    text = subtitle,
//                    style = Typography.bodySmall
//                )
//            }
        }
        IconButton(onClick = {}) {
            Icon(
                Icons.Outlined.PhotoCamera,
                "Camera Icon",
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
fun MyAppBar(
    onBackPressed: () -> Unit,
    onActivityIconClicked: () -> Unit,
) {
    TopAppBar(title = {
        Text(
            text = "Chats",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto
            ),
        )
    },
        actions = {
            IconButton(onClick = onActivityIconClicked) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Activity icon"
                )
            }
        })
}

@Composable
fun NoMessages() {
    MessageComponent(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxSize(),
        title = "No messages yet",
        subtitle = "Looks like you haven't initiated a conversation with any party buddies",
    )
}

