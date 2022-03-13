package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.profile.ProfileHeader
import com.salazar.cheers.components.profile.ProfileText
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
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
    onGiftClick: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(uiState.user, onBackPressed = onBackPressed, onCopyUrl) }
    ) {
        Column {
            Profile(
                uiState,
                onFollowClicked = onFollowClicked,
                onUnfollowClicked = onUnfollowClicked,
                onMessageClicked = onMessageClicked,
                onStatClicked = onStatClicked,
                onGiftClick = onGiftClick,
            )
            Tabs(uiState)
        }
    }
}

@Composable
fun Profile(
    uiState: OtherProfileUiState,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onMessageClicked: () -> Unit,
    onStatClicked: (statName: String, username: String) -> Unit,
    onGiftClick: () -> Unit,
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
        ProfileHeader(uiState.user, onStatClicked)
        ProfileText(user = uiState.user, onWebsiteClicked = {})
        Spacer(Modifier.height(8.dp))
        HeaderButtons(
            uiState,
            onFollowClicked = onFollowClicked,
            onUnfollowClicked = onUnfollowClicked,
            onMessageClicked = onMessageClicked,
            onGiftClick = onGiftClick,
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
        columns = Fixed(3),
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
    if (post.photos.isEmpty()) return

    Box(
        modifier = Modifier.padding(1.dp)
    ) {
        PrettyImage(
            data = post.photos[0],
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
fun HeaderButtons(
    uiState: OtherProfileUiState,
    onFollowClicked: () -> Unit,
    onUnfollowClicked: () -> Unit,
    onMessageClicked: () -> Unit,
    onGiftClick: () -> Unit,
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
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = onGiftClick) {
            Icon(
                Icons.Outlined.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
