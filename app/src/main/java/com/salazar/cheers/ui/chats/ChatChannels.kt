package com.salazar.cheers.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {

    private val viewModel: ChatChannelsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MessagesScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MessagesScreen() {
        val uiState = viewModel.uiState.collectAsState().value
        val user = mainViewModel.user2.value

        Scaffold(
            topBar = {
                Column {
                    MyAppBar(user?.username ?: "Chat")
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    findNavController().navigate(R.id.newMessageFragment)
                }) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        ) {
            Column {
                Tabs(uiState)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Tabs(uiState: ChatChannelUiState) {
        val tabs = listOf("Primary", "General", "Requests")
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
                        val channels = uiState.channels
                        if (uiState.isLoading)
                            LoadingScreen()
                        if (channels != null)
                            ConversationList(channels)
                    }
                    1 -> {}
                }
            }
        }
    }

    @Composable
    fun ConversationList(channels: List<ChatChannel>) {
        if (channels.isEmpty())
            Text("No chat channels")
        LazyColumn {
            items(channels) { channel ->
                when (channel.type) {
                    ChatChannelType.DIRECT -> DirectConversation(channel = channel)
                    ChatChannelType.GROUP -> GroupConversation(channel = channel)
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

    @ExperimentalCoilApi
    @Composable
    fun DirectConversation(channel: ChatChannel) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action =
                        MessagesFragmentDirections.actionMessagesFragmentToChatActivity(
                            channelId = channel.id,
                            name = channel.otherUser.fullName,
                            username = channel.otherUser.username,
                            profilePicturePath = channel.otherUser.profilePictureUrl,
                        )
                    findNavController().navigate(action)
                }
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
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    val title = channel.otherUser.fullName
                    val subtitle = if (isLastMessageMe) channel.recentMessage.text else
                        "${channel.recentMessage.senderUsername}: ${channel.recentMessage.text}"

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

    @ExperimentalCoilApi
    @Composable
    fun GroupConversation(channel: ChatChannel) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action =
                        MessagesFragmentDirections.actionMessagesFragmentToChatActivity(
                            channelId = channel.id,
                            name = channel.otherUser.fullName,
                            username = channel.otherUser.username,
                            profilePicturePath = channel.otherUser.profilePictureUrl,
                        )
                    findNavController().navigate(action)
                }
                .padding(15.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val image = channel.recentMessage.senderProfilePictureUrl

                Image(
                    painter = rememberImagePainter(data = image),
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
    fun MyAppBar(username: String) {
        SmallTopAppBar(
            title = {
                Text(
                    text = username,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    findNavController().popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    findNavController().navigate(R.id.activityFragment)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Activity icon"
                    )
                }
            },
        )
    }
}

