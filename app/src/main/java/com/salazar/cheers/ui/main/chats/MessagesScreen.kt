package com.salazar.cheers.ui.main.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch
import java.time.ZoneId

@Composable
fun MessagesScreen(
    uiState: MessagesUiState,
    onNewMessageClicked: () -> Unit,
    onChannelClicked: (channelId: String) -> Unit,
    onLongPress: (String) -> Unit,
    onActivityIconClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                MyAppBar({}, onActivityIconClicked)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewMessageClicked) {
                Icon(Icons.Default.Edit, "")
            }
        }
    ) {
        Column {
            Tabs(
                uiState = uiState,
                onChannelClicked = onChannelClicked,
                onLongPress = onLongPress,
            )
        }
    }
}

@Composable
fun Tabs(
    uiState: MessagesUiState,
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
) {
    val tabs = listOf("Direct", "Groups")
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, title ->
            Tab(
                icon = { Text(title, style = MaterialTheme.typography.titleSmall) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
//                        viewModel.toggle()
                },
            )
        }
    }
    HorizontalPager(
        count = tabs.size,
        state = pagerState,
    ) { page ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (page) {
                0 -> {
                    val directChats = uiState.channels?.filter { it.type == ChatChannelType.DIRECT }
                    if (uiState.isLoading)
                        LoadingScreen()
                    if (directChats != null)
                        ConversationList(directChats, onChannelClicked, onLongPress)
                }
                1 -> {
                    val groupChats = uiState.channels?.filter { it.type == ChatChannelType.GROUP }
                    if (uiState.isLoading)
                        LoadingScreen()
                    if (groupChats != null)
                        ConversationList(groupChats, onChannelClicked, onLongPress)
                }
            }
        }
    }
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

@Composable
fun ConversationList(
    channels: List<ChatChannel>,
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
) {
    if (channels.isEmpty())
        NoMessages()
    LazyColumn {
        items(channels) { channel ->
            when (channel.type) {
                ChatChannelType.DIRECT -> DirectConversation(
                    channel = channel,
                    onChannelClicked,
                    onLongPress
                )
                ChatChannelType.GROUP -> GroupConversation(channel = channel, onChannelClicked)
            }
        }
    }
}

@Composable
fun LinkContactsItem() {
    Row {
        Button(onClick = { /*TODO*/ }) {

        }
    }
}

@Composable
fun DirectConversation(
    channel: ChatChannel,
    onChannelClicked: (String) -> Unit,
    onLongPress: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onChannelClicked(channel.id) },
                onLongClick = { onLongPress(channel.otherUser.fullName) })
            .padding(15.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val image = channel.otherUser.profilePictureUrl
        val seen =
            channel.recentMessage.seenBy.contains(FirebaseAuth.getInstance().currentUser?.uid!!)
        val isLastMessageMe =
            channel.recentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid!!

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Image(
                painter = rememberImagePainter(
                    data = image,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                val title = channel.otherUser.fullName
                val seenByOthers = channel.recentMessage.seenBy.size > 1
                val lastMessageStatus = if (isLastMessageMe && seenByOthers) "Opened"
                else if (isLastMessageMe && !seenByOthers) "Delivered"
                else if (!isLastMessageMe) "Received"
                else "New chat"

//                val subtitle = if (isLastMessageMe) channel.recentMessage.text else
//                    "$lastMessageStatus ${channel.recentMessage.senderUsername}: ${channel.recentMessage.text}"
                val subtitle = buildAnnotatedString {
                    append(lastMessageStatus)
                    append("  •  ")
                    append(
                        relativeTimeFormatter(
                            value = channel.recentMessageTime?.toDate()?.toInstant()
                                ?.atZone(ZoneId.systemDefault())!!.toString()
                        )
                    )
//                    append("  •  ")
//                    withStyle(
//                        style = SpanStyle(
//                            color = MaterialTheme.colorScheme.onBackground,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 14.sp,
//                        )
//                    ) {
//                        append("189\uD83E\uDE85")
//                    }
                }

                val fontWeight = if (seen) FontWeight.Normal else FontWeight.Bold
                Text(text = title, style = Typography.bodyMedium, fontWeight = fontWeight)
                Text(
                    text = subtitle,
                    style = Typography.bodySmall,
                    fontWeight = fontWeight,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
        val tint =
            if (seen) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onBackground

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!seen) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0095F6))
                )
                Spacer(Modifier.width(12.dp))
            }

            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    "Camera Icon",
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
            val image = channel.recentMessage.senderProfilePictureUrl

            Image(
                painter = rememberImagePainter(
                    data = image,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_group_picture)
                    },
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Column {
                val title = channel.name
                val subtitle =
                    "${channel.recentMessage.senderUsername}: ${channel.recentMessage.text}"
                Text(text = title, style = Typography.bodyMedium)
                Text(
                    text = subtitle,
                    style = Typography.bodySmall
                )
            }
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
//        navigationIcon = {
//            IconButton(onClick = onBackPressed) {
//                Icon(
//                    imageVector = Icons.Outlined.ArrowBack,
//                    contentDescription = null,
//                )
//            }
//        },
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
