package com.salazar.cheers.ui.otherprofile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.profile.OtherProfileUiState
import com.salazar.cheers.ui.profile.OtherProfileViewModel
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.StorageUtil

class OtherProfileFragment : Fragment() {

    private val viewModel: OtherProfileViewModel by viewModels()
    private val args: OtherProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                OtherProfileScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUser(args.otherUserId)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OtherProfileScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        Scaffold(
            topBar = { Toolbar(uiState.user) }
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                if (uiState.isLoading)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                Section1(uiState)
                Section2(uiState.user)
                HeaderButtons(uiState)
            }
        }
    }

    @Composable
    fun Toolbar(otherUser: User) {
        SmallTopAppBar(
            title = { Text(otherUser.username, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
            navigationIcon = {
                IconButton(onClick = { findNavController().popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
            actions = { },
        )
    }

    @Composable
    fun Section2(otherUser: User) {
        Column {
            Text(
                text = "${otherUser.firstName} ${otherUser.lastName}",
                style = Typography.bodyMedium
            )
            Text(otherUser.bio)
            Text(otherUser.website)
        }
    }

    @Composable
    fun Section1(uiState: OtherProfileUiState) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {
            val photo = remember { mutableStateOf<Uri?>(null) }

            if (uiState.user.profilePicturePath.isNotBlank())
                StorageUtil.pathToReference(uiState.user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                    photo.value = it
                }
            Image(
                painter = rememberImagePainter(
                    data = photo.value,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                modifier = Modifier.size(70.dp),
                contentDescription = null,
            )
            Counters(uiState)
        }
    }

    @Composable
    fun Counters(uiState: OtherProfileUiState) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            val otherUser = uiState.user
            val items = listOf(
                Counter("Posts", otherUser.posts),
                Counter("Followers", otherUser.followers),
                Counter("Following", otherUser.following),
            )

            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = item.value.toString(), fontWeight = FontWeight.Bold)
                    Text(text = item.name)
                }
            }
        }
    }

    @Composable
    fun HeaderButtons(uiState: OtherProfileUiState) {
        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.followUser(uiState.user.id)
                }
            ) {
                if (uiState.isFollowing)
                    Text("Following")
                else
                    Text("Follow")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    FirestoreChat.getOrCreateChatChannel(uiState.user) { channelId ->
                        val action =
                            OtherProfileFragmentDirections.actionOtherProfileFragmentToChatActivity(
                                chatChannelId = channelId
                            )
                        findNavController().navigate(action)
                    }
                }
            ) {
                Text("Message")
            }
        }
    }
}
