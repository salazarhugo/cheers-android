package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.components.FunctionalityNotAvailablePanel
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.buttons.CheersOutlinedButton
import com.salazar.cheers.components.profile.ProfileHeader
import com.salazar.cheers.components.profile.ProfileText
import com.salazar.cheers.components.user.FollowButton
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch

@Composable
fun OtherProfileScreen(
    uiState: OtherProfileUiState.HasUser,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onFollowToggle: (User) -> Unit,
    onMessageClicked: () -> Unit,
    onWebsiteClick: (String) -> Unit,
    onPostMoreClicked: (String, String) -> Unit,
    onGiftClick: () -> Unit,
    onStoryClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    val posts = uiState.posts
    val pagerState = rememberPagerState()
    val tabs = listOf(
        Icons.Outlined.ViewList,
        Icons.Default.GridView,
        Icons.Outlined.Celebration
    )

    LazyColumn {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ProfileHeader(user = uiState.user, onStatClicked = onStatClicked, onStoryClick = onStoryClick)
                ProfileText(user = uiState.user, onWebsiteClicked = onWebsiteClick)
                val isFollowed = uiState.user.followBack
                HeaderButtons(
                    isFollowed = isFollowed,
                    onFollowToggle = { onFollowToggle(uiState.user) },
                    onMessageClicked = onMessageClicked,
                    onGiftClick = onGiftClick,
                )
            }
        }
        stickyHeader {
            val scope = rememberCoroutineScope()
            androidx.compose.material.TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                tabs.forEachIndexed { index, icon ->
                    androidx.compose.material.Tab(
                        icon = { Icon(icon, contentDescription = null) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        }

        item {
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (page) {
                        0 -> posts?.forEach { postFeed ->
                            com.salazar.cheers.ui.main.profile.Post(
                                post = postFeed,
                                onPostLike = onPostLike,
                                onPostClicked = onPostClicked,
                                onPostMoreClicked = onPostMoreClicked,
                                onCommentClick = { onCommentClick(postFeed.id)},
                            )
                        }
                        1 -> FunctionalityNotAvailablePanel()
                        2 -> FunctionalityNotAvailablePanel()
                    }
                }
            }
        }
    }
}

@Composable
fun Toolbar(
    username: String,
    verified: Boolean,
    onBackPressed: () -> Unit,
    onCopyUrl: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }
    SmallTopAppBar(
        title = {
            Username(
                username = username,
                verified = verified,
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
    isFollowed: Boolean,
    onFollowToggle: () -> Unit,
    onMessageClicked: () -> Unit,
    onGiftClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        FollowButton(
            isFollowing = isFollowed,
            onClick = onFollowToggle,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(6.dp))
        CheersOutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onMessageClicked,
        ) {
            Text("Message")
        }
//        Spacer(modifier = Modifier.width(12.dp))
//        IconButton(onClick = onGiftClick) {
//            Icon(
//                Icons.Outlined.CardGiftcard,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary
//            )
//        }
    }
}
