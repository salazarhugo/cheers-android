package com.salazar.cheers.ui.main.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.RoomStatus
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.chat.*
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.compose.user.FollowButton
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.User
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.ui.theme.BlueCheers
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography


@Composable
fun MessagesScreen(
    uiState: MessagesUiState,
    onNewChatClicked: () -> Unit,
    onChannelClicked: (channelId: String) -> Unit,
    onLongPress: (String) -> Unit,
    onFollowToggle: (User) -> Unit,
    onUserClick: (String) -> Unit,
    onActivityIconClicked: () -> Unit,
    onSwipeRefresh: () -> Unit,
    onCameraClick: (String) -> Unit,
    onSearchInputChange: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                MyAppBar({}, onActivityIconClicked)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewChatClicked) {
                Icon(Icons.Default.Edit, "")
            }
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = { onSwipeRefresh() },
            modifier = Modifier.padding(it),
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Tabs(
                    uiState = uiState,
                    onChannelClicked = onChannelClicked,
                    onLongPress = onLongPress,
                    onFollowToggle = onFollowToggle,
                    onUserClick = onUserClick,
                    onCameraClick = onCameraClick,
                    onSearchInputChange = onSearchInputChange,
                )
            }
        }
    }
}

@Composable
fun Tabs(
    uiState: MessagesUiState,
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onFollowToggle: (User) -> Unit,
    onUserClick: (String) -> Unit,
    onCameraClick: (String) -> Unit,
    onSearchInputChange: (String) -> Unit,
) {
    val suggestions = uiState.suggestions

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        if (uiState.isLoading)
            LoadingScreen()

        SearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            searchInput = uiState.searchInput,
            onSearchInputChanged = onSearchInputChange,
            placeholder = {
                Text("Search")
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
        )

        if (uiState.channels != null)
            ConversationList(
                channels = uiState.channels,
                onChannelClicked = onChannelClicked,
                onLongPress = onLongPress,
                suggestions = suggestions,
                onFollowToggle = onFollowToggle,
                onUserClick = onUserClick,
                onCameraClick = onCameraClick,
            )
    }
}

@Composable
fun ConversationList(
    channels: List<ChatChannel>,
    suggestions: List<User>?,
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onFollowToggle: (User) -> Unit,
    onUserClick: (String) -> Unit,
    onCameraClick: (String) -> Unit,
) {
    if (channels.isEmpty())
        NoMessages()

    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        items(channels, key = { it.id }) { channel ->
            DirectConversation(
                modifier = Modifier.animateItemPlacement(),
                channel = channel,
                onChannelClicked,
                onLongPress,
                onCameraClick = onCameraClick,
            )
        }
        if (suggestions != null && suggestions.isNotEmpty())
            item {
                Text(
                    text = "Find friends to follow and message",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        if (suggestions != null)
            items(suggestions) { user ->
                UserItem(
                    user = user,
                    onUserClick = onUserClick,
                    onFollowToggle = onFollowToggle,
                )
            }
    }
}

@Composable
fun UserItem(
    user: User,
    isAuthor: Boolean = false,
    onUserClick: (String) -> Unit,
    onFollowToggle: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick(user.username) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfilePicture(avatar = user.profilePictureUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
//                    Text(text = user.username, style = Typography.bodyMedium)
            }
        }
        if (isAuthor)
            Image(
                rememberAsyncImagePainter(R.drawable.ic_crown),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(16.dp),
                contentDescription = null,
            )
        FollowButton(isFollowing = user.followBack, onClick = { onFollowToggle(user) })
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
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onCameraClick: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onChannelClicked(channel.id) },
                onLongClick = {
                    onLongPress(channel.id)
                })
            .padding(15.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserProfilePicture(
                avatar = channel.avatarUrl,
                size = 50.dp,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                val title = channel.name

                val subtitle = buildAnnotatedString {
                    append("  •  ")
                    append(
                        relativeTimeFormatter(
                            timestamp = channel.recentMessageTime.seconds
                        )
                    )
                }

                val fontWeight =
                    if (channel.status == RoomStatus.NEW) FontWeight.Bold else FontWeight.Normal
//                if (title.isNotBlank())
//                    Text(
//                        text = title,
//                        style = Typography.bodyMedium,
//                        fontWeight = fontWeight,
//                    )
//                else
                Username(username = channel.name, verified = channel.verified)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(channel.recentMessageType) {
                        when (channel.status) {
                            RoomStatus.NEW -> NewChat(this)
                            RoomStatus.EMPTY -> EmptyChat()
                            RoomStatus.OPENED -> OpenedChat()
                            RoomStatus.SENT -> DeliveredChat(this)
                            RoomStatus.RECEIVED -> ReceivedChat(this)
                            RoomStatus.SENDING -> SendingChat()
                            RoomStatus.UNRECOGNIZED -> {}
                        }
                    }
                    Text(
                        text = subtitle,
                        style = Typography.bodySmall,
                        fontWeight = fontWeight,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
        val tint =
            if (channel.status == RoomStatus.NEW) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (channel.status == RoomStatus.NEW) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BlueCheers)
                )
                Spacer(Modifier.width(12.dp))
            }

            val icon =
                if (channel.status == RoomStatus.NEW) Icons.Outlined.Sms else Icons.Outlined.PhotoCamera

            IconButton(onClick = { onCameraClick(channel.id) }) {
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
    SmallTopAppBar(
        title = {
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
        },
    )
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

