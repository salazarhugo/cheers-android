package com.salazar.cheers.ui.otherprofile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.R
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.profile.OtherProfileUiState
import com.salazar.cheers.ui.profile.OtherProfileViewModel
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.StorageUtil
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

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

        viewModel.refreshUser(args.username)
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Toolbar(otherUser: User) {
        val openDialog = remember { mutableStateOf(false)  }
        SmallTopAppBar(
            title = {
                Username(
                    username = otherUser.username,
                    verified = otherUser.verified,
                    textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Roboto),
                )
            },
            navigationIcon = {
                IconButton(onClick = { findNavController().popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
            actions = {
                IconButton(onClick = {
//                    findNavController().navigate(R.id.moreOtherProfileBottomSheet)
                    openDialog.value = true
                }) {
                    Icon(Icons.Default.MoreVert, "")
                }
            },
        )
        if (openDialog.value)
            MoreDialog(openDialog = openDialog)
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MoreDialog(
        openDialog: MutableState<Boolean>,
    ) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Column() {
                    TextButton(
                        onClick = { openDialog.value = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Report")
                    }
                    TextButton(
                        onClick = { openDialog.value = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Report")
                    }
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Block")
                    }
                    TextButton(
                        onClick = {
                            copyProfileUrl(args.username)
                            openDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Copy Profile URL")
                    }
                    TextButton(
                        onClick = { openDialog.value = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Send Profile as Message")
                    }
                }
            },
            confirmButton = {
            },
        )
    }

    private fun copyProfileUrl(username: String) {
        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.parse("https://cheers.app/$username")
            domainUriPrefix = "https://console.salazar-ci.com/link"
            // Open links with this app on Android
            androidParameters {
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", shortLink.toString())
            clipboardManager.setPrimaryClip(clipData)
            toast("Copied profile URL to clipboard")
        }.addOnFailureListener {
            toast(it.toString())
        }
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
                                channelId = channelId,
                                name = uiState.user.fullName,
                                username = uiState.user.username,
                                profilePicturePath = uiState.user.profilePicturePath,
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
