package com.salazar.cheers.ui.otherprofile

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.R
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun OtherProfileScreen(
    uiState: OtherProfileUiState,
    modifier: Modifier = Modifier,
    onSwipeRefresh: () -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onStatClicked: (statName: String, username: String) -> Unit,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onMessageClicked: () -> Unit,
    onBackPressed: () -> Unit,
    onCopyUrl: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(uiState.user, onBackPressed = onBackPressed, onCopyUrl) }
    ) {
        Column {
            ProfileHeader(
                uiState,
                onFollowClicked = onFollowClicked,
                onUnfollowClicked = onUnfollowClicked,
                onMessageClicked = onMessageClicked,
                onStatClicked = onStatClicked,
            )
            Tabs(uiState)
        }
    }
}

@Composable
fun ProfileHeader(
    uiState: OtherProfileUiState,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onMessageClicked: () -> Unit,
    onStatClicked: (statName: String, username: String) -> Unit,
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
        Section1(uiState, onStatClicked)
        Section2(uiState.user)
        HeaderButtons(
            uiState,
            onFollowClicked = onFollowClicked,
            onUnfollowClicked = onUnfollowClicked,
            onMessageClicked = onMessageClicked,
        )
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
fun Toolbar(
    otherUser: User,
    onBackPressed: () -> Unit,
    onCopyUrl: () -> Unit,
) {
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
            IconButton(onClick = onBackPressed) {
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
        MoreDialog(openDialog = openDialog, onCopyUrl = onCopyUrl)
}

@Composable
fun MoreDialog(
    openDialog: MutableState<Boolean>,
    onCopyUrl: () -> Unit,
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
                        onCopyUrl()
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


@Composable
fun Section2(otherUser: User) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row() {
            Text(
                text = otherUser.fullName,
                style = Typography.bodyMedium
            )
            if (otherUser.verified) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "VIP",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
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
fun Section1(
    uiState: OtherProfileUiState,
    onStatClicked: (statName: String, username: String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
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
        Counters(uiState, onStatClicked)
    }
}

@Composable
fun Counters(
    uiState: OtherProfileUiState,
    onStatClicked: (statName: String, username: String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        val otherUser = uiState.user
        val items = listOf(
            Counter("Posts", otherUser.postCount),
            Counter("Followers", otherUser.followers),
            Counter("Following", otherUser.following),
        )

        items.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStatClicked(item.name, otherUser.username) }
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
fun HeaderButtons(
    uiState: OtherProfileUiState,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onMessageClicked: () -> Unit,
) {
    Row {
        if (uiState.user.isFollowed)
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onUnfollowClicked,
            ) {
                Text(text = "Following")
            }
        else
            Button(
                modifier = Modifier.weight(1f),
                onClick = onFollowClicked
            ) {
                Text("Follow")
            }
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onMessageClicked,
        ) {
            Text("Message")
        }
    }
}
