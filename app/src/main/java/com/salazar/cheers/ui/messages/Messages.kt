package com.salazar.cheers.ui.messages

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.chat.ChatActivity
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.Neo4jUtil
import com.salazar.cheers.util.StorageUtil
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by viewModels()

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

    @Composable
    fun MessagesScreen() {
        val conversations = viewModel.conversations.collectAsState(initial = listOf()).value

        Column() {
            Text("Chats", modifier= Modifier.padding(15.dp))
            ConversationList(channels = conversations)
        }
    }

    @Composable
    fun ConversationList(channels: List<ChatChannel>) {
        LazyColumn() {
            items(channels) { channel ->
                Conversation(channel)
            }
        }
    }

    @Composable
    fun LinkContactsItem() {
        Row() {
            Button(onClick = { /*TODO*/ }) {
                
            }
        }
    }
    
    @ExperimentalCoilApi
    @Composable
    fun Conversation(channel: ChatChannel) {
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val photo = remember { mutableStateOf<Uri?>(null) }

                if (channel.recentMessage.authorImage.isNotBlank())
                    StorageUtil.pathToReference(channel.recentMessage.authorImage).downloadUrl.addOnSuccessListener {
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
                Column() {
                    var title = channel.name
                    var subtitle = "${channel.recentMessage.senderUsername}: ${channel.recentMessage.text}"
                    if (channel.type == ChatChannelType.DIRECT)
                    {
                        title = channel.recentMessage.senderUsername
                        subtitle = channel.recentMessage.text
                    }

                    Text(text = title, style = Typography.bodyMedium)
                    Text(
                        text = subtitle,
                        style = Typography.bodySmall
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.PhotoCamera, "Camera Icon")
            }
        }
    }
}

