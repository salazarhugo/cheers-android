package com.salazar.cheers.ui.main.otherprofile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.ui.compose.chat.FunctionalityNotAvailablePanel
import com.salazar.cheers.ui.compose.Username
import com.salazar.cheers.ui.compose.buttons.CheersOutlinedButton
import com.salazar.cheers.ui.compose.profile.ProfileHeader
import com.salazar.cheers.ui.compose.profile.ProfileText
import com.salazar.cheers.ui.compose.user.FollowButton
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.compose.user.FriendButton
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun OtherProfileScreen(
    uiState: OtherProfileUiState.HasUser,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    onSendFriendRequest: (String) -> Unit,
    onCancelFriendRequest: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
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
    val user = uiState.user

    LazyColumn {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ProfileHeader(
                    user = user,
                    onStatClicked = onStatClicked,
                    onStoryClick = onStoryClick,
                    onWebsiteClick = onWebsiteClick,
                )
                HeaderButtons(
                    friend = user.friend,
                    requested = user.requested,
                    hasRequestedViewer = user.hasRequestedViewer,
                    onSendFriendRequest = {
                        onSendFriendRequest(uiState.user.id)
                    },
                    onCancelFriendRequest = {
                        onCancelFriendRequest(uiState.user.id)
                    },
                    onAcceptFriendRequest = {
                        onAcceptFriendRequest(uiState.user.id)
                    },
                    onMessageClicked = onMessageClicked,
                )
            }
        }
        if (user.friend)
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

        if (user.friend)
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
                                onCommentClick = { onCommentClick(postFeed.id) },
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
    onRemoveFriend: () -> Unit,
    onManageFriendship: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }
    //                    findNavController().navigate(R.id.moreOtherProfileBottomSheet)
    TopAppBar(title = {
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
                Icon(Icons.Default.MoreVert, null)
            }
        })
    if (openDialog.value)
        MoreDialog(
            openDialog = openDialog,
            onCopyUrl = onCopyUrl,
            onRemoveFriend = onRemoveFriend,
            onManageFriendship = onManageFriendship,
        )
}

@Composable
fun MoreDialog(
    openDialog: MutableState<Boolean>,
    onCopyUrl: () -> Unit,
    onRemoveFriend: () -> Unit,
    onManageFriendship: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        text = {
            Column {
                TextButton(
                    onClick = {
                        onRemoveFriend()
                        openDialog.value = false
                      },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Remove friend")
                }
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onManageFriendship()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Manage Friendship")
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
    friend: Boolean,
    requested: Boolean,
    hasRequestedViewer: Boolean,
    onCancelFriendRequest: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onAcceptFriendRequest: () -> Unit,
    onMessageClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FriendButton(
            friend = friend,
            requested = requested,
            hasRequestedViewer = hasRequestedViewer,
            modifier = Modifier.weight(1f),
            onCancelFriendRequest = onCancelFriendRequest,
            onSendFriendRequest = onSendFriendRequest,
            onAcceptFriendRequest = onAcceptFriendRequest,
        )
        if (friend)
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
