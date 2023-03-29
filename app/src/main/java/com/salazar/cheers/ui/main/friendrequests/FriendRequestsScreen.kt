package com.salazar.cheers.ui.main.friendrequests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.buttons.CheersOutlinedButton
import com.salazar.cheers.ui.compose.items.UserItem
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.compose.text.MyText
import com.salazar.cheers.ui.main.party.create.TopAppBar
import com.salazar.cheers.user.ui.AddFriendButton

@Composable
fun FriendRequestsScreen(
    uiState: FriendRequestsUiState,
    onFriendRequestsUIAction: (FriendRequestsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onDismiss = { onFriendRequestsUIAction(FriendRequestsUIAction.OnBackPressed)},
                title = "Friend requests"
            )
        }
    ) {
        if (uiState.isLoading)
            LoadingScreen()
        else
            SwipeToRefresh(
                state = rememberSwipeToRefreshState(uiState.isRefreshing),
                onRefresh = { onFriendRequestsUIAction(FriendRequestsUIAction.OnSwipeRefresh)},
                modifier = Modifier.padding(it),
            ) {
                val friendRequests = uiState.friendRequests
                if (friendRequests != null)
                    FriendRequestList(
                        suggestions = uiState.suggestions,
                        friendRequests = friendRequests,
                        onFriendRequestsUIAction = onFriendRequestsUIAction,
                    )
            }
    }
}

@Composable
fun FriendRequestList(
    suggestions: List<UserItem>?,
    friendRequests: List<UserItem>,
    onFriendRequestsUIAction: (FriendRequestsUIAction) -> Unit
) {
    LazyColumn {
        items(friendRequests) { user ->
            UserItem(
                modifier = Modifier.animateItemPlacement(),
                userItem = user,
                onClick = {
                },
                content = {
                    FriendRequestButtons(
                        user = user,
                        onFriendRequestsUIAction = onFriendRequestsUIAction,
                    )
                }
            )
        }
        if (suggestions != null) {
            item {
                MyText(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
            items(items = suggestions) { user ->
                UserItem(
                    userItem = user,
                    onClick = {
                        onFriendRequestsUIAction(FriendRequestsUIAction.OnUserClick(user.username))
                    },
                    content = {
                        AddFriendButton(
                            requestedByViewer = user.requested,
                            onAddFriendClick = {
                                onFriendRequestsUIAction(FriendRequestsUIAction.OnAddFriendClick(user.id))
                            },
                            onCancelFriendRequestClick = {
                                onFriendRequestsUIAction(FriendRequestsUIAction.OnCancelFriendRequestClick(user.id))
                            },
                            onDelete = {
                                onFriendRequestsUIAction(FriendRequestsUIAction.OnRemoveSuggestion(user))
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FriendRequestButtons(
    user: UserItem,
    onFriendRequestsUIAction: (FriendRequestsUIAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CheersOutlinedButton(
            onClick = {
                onFriendRequestsUIAction(FriendRequestsUIAction.OnAcceptFriendRequest(user.id))
            },
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
        }
        IconButton(
            modifier = Modifier.height(34.dp),
            onClick = {
                onFriendRequestsUIAction(FriendRequestsUIAction.OnRefuseFriendRequest(user.id))
            },
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
    }
}