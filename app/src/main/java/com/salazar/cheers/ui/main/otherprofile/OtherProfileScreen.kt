package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.profile.ProfileHeader
import com.salazar.cheers.components.profile.ProfileText
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.profile.ProfilePostsAndTags
import com.salazar.cheers.ui.theme.Roboto

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
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = onSwipeRefresh,
    ) {
        Scaffold(
            topBar = { Toolbar(uiState.user, onBackPressed = onBackPressed, onCopyUrl) }
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Profile(
                    uiState,
                    onFollowClicked = onFollowClicked,
                    onUnfollowClicked = onUnfollowClicked,
                    onMessageClicked = onMessageClicked,
                    onStatClicked = onStatClicked,
                    onGiftClick = onGiftClick,
                )
            }
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
    if (uiState is OtherProfileUiState.HasPosts)
        ProfilePostsAndTags(
            posts = uiState.posts,
            onPostClicked = {},
        )
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
