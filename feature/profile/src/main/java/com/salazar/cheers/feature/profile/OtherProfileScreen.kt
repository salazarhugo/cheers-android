package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import com.salazar.cheers.core.ui.PartyItem
import com.salazar.cheers.core.ui.item.PostItem
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.data.post.repository.Post
import kotlinx.coroutines.launch

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
    val parties = uiState.parties
    val tabs = listOf(
        Icons.Outlined.ViewList,
        Icons.Default.GridView,
        Icons.Outlined.Celebration
    )
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
    )
    val user = uiState.user

    LazyColumn {
        item {
            Column() {
                ProfileHeader(
                    user = user,
                    isEditable = false,
                    onStatClicked = onStatClicked,
                    onWebsiteClick = onWebsiteClick,
                    onEditProfileClick = {},
                )
//                AdminButtons(
//                    modifier = Modifier.fillMaxWidth(),
//                )
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
        if (user.friend || user.isBusinessAccount)
        stickyHeader {
            val scope = rememberCoroutineScope()
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
//                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                tabs.forEachIndexed { index, icon ->
                    Tab(
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

        if (user.friend || user.isBusinessAccount)
        item {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (page) {
                        0 -> posts?.forEach { postFeed ->
                            PostItem(
                                post = postFeed,
//                                onPostLike = onPostLike,
//                                onPostClicked = onPostClicked,
//                                onPostMoreClicked = onPostMoreClicked,
//                                onCommentClick = { onCommentClick(postFeed.id) },
                            )
                        }
                        1 -> parties?.forEach { party ->
                            PartyItem(
                                party = party,
                                onEventClicked = {},
                                onMoreClick = {},
                            )
                        }
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
    onManageFriendship: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }
    //                    findNavController().navigate(R.id.moreOtherProfileBottomSheet)
    TopAppBar(title = {
        com.salazar.cheers.core.share.ui.Username(
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
            onManageFriendship = onManageFriendship,
        )
}

@Composable
fun MoreDialog(
    openDialog: MutableState<Boolean>,
    onCopyUrl: () -> Unit,
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
fun AdminButtons(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = { /*TODO*/ },
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            CheersOutlinedButton(
                onClick = {},
            ) {
                Text("Verify User")
            }
        }
    }
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
