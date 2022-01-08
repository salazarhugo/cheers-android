package com.salazar.cheers.ui.otherprofile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.R
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreChat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment : Fragment() {

    private val args: OtherProfileFragmentArgs by navArgs()

    @Inject
    lateinit var otherProfileViewModelFactory: OtherProfileViewModel.OtherProfileViewModelFactory

    private val viewModel: OtherProfileViewModel by viewModels {
        OtherProfileViewModel.provideFactory(otherProfileViewModelFactory, args.username)
    }

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OtherProfileScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        Scaffold(
            topBar = { Toolbar(uiState.user) }
        ) {
            Column {
                ProfileHeader(uiState)
                Tabs(uiState)
            }
        }
    }

    @Composable
    fun ProfileHeader(uiState: OtherProfileUiState) {
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

    @Composable
    fun Tabs(uiState: OtherProfileUiState) {
        val tabs = listOf(Icons.Default.GridView, Icons.Outlined.AssignmentInd)
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
            tabs.forEachIndexed { index, icon ->
                Tab(
                    icon = { Icon(icon, null) },
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
                        if (uiState is OtherProfileUiState.HasPosts)
                            GridViewPosts(posts = uiState.posts)
                    }
                    1 -> {}
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun GridViewPosts(posts: List<Post>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(3),
            modifier = Modifier,
        ) {
            val imagePosts = posts.filter { it.type == PostType.IMAGE }
            items(imagePosts) { post ->
                PostItem(post)
            }
        }
    }

    @Composable
    fun PostItem(post: Post) {
        Box(
            modifier = Modifier.padding(1.dp)
        ) {
            PrettyImage(
                data = post.photoUrl,
                contentDescription = "avatar",
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)// or 4/5f
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { })
                    }
            )
        }
    }

    @Composable
    fun Toolbar(otherUser: User) {
        val openDialog = remember { mutableStateOf(false) }
        SmallTopAppBar(
            title = {
                Username(
                    username = otherUser.username,
                    verified = otherUser.verified,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    ),
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

    @Composable
    fun MoreDialog(
        openDialog: MutableState<Boolean>,
    ) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Column {
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
            link = Uri.parse("https://cheers-a275e.web.app/$username")
            domainUriPrefix = "https://cheers2cheers.page.link"
            // Open links with this app on Android
            androidParameters {
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val clipboardManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
            Text(
                otherUser.bio,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
            ClickableText(
                text = AnnotatedString(otherUser.website),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Normal
                ),
                onClick = { offset ->
                },
            )
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
            Image(
                painter = rememberImagePainter(
                    data = uiState.user.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val action =
                            OtherProfileFragmentDirections.actionOtherProfileFragmentToOtherFollowersFollowing(
                                username = otherUser.username,
                                goToFollowing = item.name == "Following"
                            )
                        findNavController().navigate(action)
                    }
                ) {
                    Text(
                        text = item.value.toString(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                    Text(text = item.name, fontSize = 14.sp, fontFamily = Roboto)
                }
            }
        }
    }

    @Composable
    fun HeaderButtons(uiState: OtherProfileUiState) {
        Row {
            if (uiState.user.isFollowed)
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.unfollowUser() },
                ) {
                    Text(text = "Following")
                }
            else
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.followUser() }
                ) {
                    Text("Follow")
                }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    FirestoreChat.getOrCreateChatChannel(uiState.user) { channelId ->
                        val action =
                            OtherProfileFragmentDirections.actionOtherProfileFragmentToChatActivity(
                                channelId = channelId,
                                name = uiState.user.fullName,
                                username = uiState.user.username,
                                profilePicturePath = uiState.user.profilePictureUrl,
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
