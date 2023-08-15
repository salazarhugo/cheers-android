package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.core.util.relativeTimeFormatter
import com.salazar.cheers.feature.chat.ui.components.SwipeableChatItem
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
import com.salazar.cheers.feature.chat.domain.models.RoomStatus
import com.salazar.cheers.feature.chat.ui.components.DeliveredChat
import com.salazar.cheers.feature.chat.ui.components.EmptyChat
import com.salazar.cheers.feature.chat.ui.components.NewChat
import com.salazar.cheers.feature.chat.ui.components.OpenedChat
import com.salazar.cheers.feature.chat.ui.components.ReceivedChat


@Composable
fun MessagesScreen(
    uiState: MessagesUiState,
    onNewChatClicked: () -> Unit,
    onRoomsUIAction: (RoomsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
//                onBackPressed = { onRoomsUIAction(RoomsUIAction.OnBackPressed) },
                title = {
                    Text(text = "Chat")
                },
                actions = {
                    IconButton(onClick = onNewChatClicked) {
                        Icon(Icons.Default.Add, null)
                    }
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

        if (uiState.isLoading)
            com.salazar.cheers.core.share.ui.LoadingScreen()

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

    if (channels.isEmpty())
        NoMessages()

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        items(items = channels, key = { it.id }) { channel ->
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onRoomsUIAction(RoomsUIAction.OnPinRoom(channel.id))
                        return@rememberDismissState false
                    }
                    true
                }
            )

            SwipeableChatItem(
                modifier = Modifier.animateItemPlacement(),
                dismissState = dismissState,
            ) {
                DirectConversation(
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
fun DirectConversation(
    modifier: Modifier = Modifier,
    channel: ChatChannel,
    onRoomsUIAction: (RoomsUIAction) -> Unit,
) {
    val backgroundColor = if (channel.pinned)
        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    else
        MaterialTheme.colorScheme.background

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = { onRoomsUIAction(RoomsUIAction.OnRoomClick(channel.id)) },
                onLongClick = {
                    onRoomsUIAction(RoomsUIAction.OnRoomLongPress(channel.id))
                })
            .padding(15.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserProfilePicture(
                picture = channel.picture,
                size = 50.dp,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                val subtitle = buildAnnotatedString {
                    append("  •  ")
                    append(relativeTimeFormatter(epoch = channel.lastMessageTime))
                }

                val fontWeight =
                    if (channel.status == RoomStatus.NEW) FontWeight.Bold else FontWeight.Normal

                com.salazar.cheers.core.share.ui.Username(
                    username = channel.name,
                    verified = channel.verified
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(channel.lastMessageType) {
                        when (channel.status) {
                            RoomStatus.NEW -> NewChat(this)
                            RoomStatus.EMPTY -> EmptyChat()
                            RoomStatus.OPENED -> OpenedChat()
                            RoomStatus.SENT -> DeliveredChat(this)
                            RoomStatus.RECEIVED -> ReceivedChat(this)
//                            RoomStatus.SENDING -> SendingChat()
                            RoomStatus.UNRECOGNIZED -> {}
                        }
                    }
                    if (channel.status != RoomStatus.EMPTY)
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = fontWeight,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                }
            }
        }
        val tint = MaterialTheme.colorScheme.outline

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (channel.status == RoomStatus.NEW) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(com.salazar.cheers.core.share.ui.BlueCheers)
                )
                Spacer(Modifier.width(12.dp))
            }

            val icon =
                if (channel.pinned)
                    Icons.Outlined.PinDrop
                else if (channel.status == RoomStatus.NEW)
                    Icons.Outlined.Sms
                else
                    Icons.Outlined.PhotoCamera

            IconButton(
                onClick = { onRoomsUIAction(RoomsUIAction.OnCameraClick(channel.id)) },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Camera Icon",
                    tint = tint,
                )
            }
        }
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
    Column(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.Inbox,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = "No messages yet",
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = "Looks like you haven't initiated a conversation with any party buddies",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

