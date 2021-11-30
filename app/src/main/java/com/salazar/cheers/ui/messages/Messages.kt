package com.salazar.cheers.ui.messages

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.chat.ChatActivity
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.Neo4jUtil
import org.jetbrains.anko.support.v4.startActivity

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
        viewModel.queryUsers("")
        val conversations = viewModel.conversation.value
        Column() {
            Text("Chats", modifier= Modifier.padding(15.dp))
            ConversationList(user = conversations)
        }
    }

    @Composable
    fun ConversationList(user: List<User>) {
        LazyColumn() {
            items(user) { user ->
                Conversation(user)
            }
        }
    }

    @Composable
    fun Conversation(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    startActivity<ChatActivity>()
//                    findNavController().navigate(R.id.chatFragment2)
                }
                .padding(15.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = rememberImagePainter(data = user.photoUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray)
                )
                Column() {
                    Text( text = user.username, style = Typography.bodyMedium)
                    Text( text = "Mdr", style = Typography.bodySmall)
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.PhotoCamera, "Camera Icon")
            }
        }
    }
}

