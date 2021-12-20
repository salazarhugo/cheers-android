package com.salazar.cheers.ui.chats

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil

class MessagesFragment : Fragment() {

    private val viewModel: ChatChannelsViewModel by viewModels()

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

        Scaffold(
            topBar = {
                Column {
                    MyAppBar()
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
                if (uiState.isLoading)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                when (uiState) {
                    is ChatChannelUiState.HasChannels -> ConversationList(channels = uiState.channels)
                    is ChatChannelUiState.NoChannels -> {
                        Text("No Channels")
                    }
                }
            }
        }
    }

    @Composable
    fun ConversationList(channels: List<ChatChannel>) {
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
                            chatChannelId = channel.id,
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
                val photo = remember { mutableStateOf<Uri?>(null) }

                val image = channel.otherUser.profilePicturePath

                if (image.isNotBlank())
                    StorageUtil.pathToReference(image)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(data = photo.value),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    val title = channel.otherUser.username
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

    @ExperimentalCoilApi
    @Composable
    fun GroupConversation(channel: ChatChannel) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action =
                        MessagesFragmentDirections.actionMessagesFragmentToChatActivity(
                            chatChannelId = channel.id,
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
                val photo = remember { mutableStateOf<Uri?>(null) }

                val image = channel.recentMessage.senderProfilePicturePath

                if (image.isNotBlank())
                    StorageUtil.pathToReference(image)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(data = photo.value),
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
    fun MyAppBar() {
        SmallTopAppBar(
            title = {
                Text(
                    text = "Chat",
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
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
    }
}

