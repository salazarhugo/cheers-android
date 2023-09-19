package com.salazar.cheers.feature.friend_request

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.text.MyText
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.user.ui.AddFriendButton

@Composable
fun FriendRequestsScreen(
    uiState: FriendRequestsUiState,
    onFriendRequestsUIAction: (FriendRequestsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Friend requests",
                onBackPressed = { onFriendRequestsUIAction(FriendRequestsUIAction.OnBackPressed)},
            )
        }
    ) {
        if (uiState.isLoading)
            com.salazar.cheers.core.share.ui.LoadingScreen()
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
    suggestions: List<com.salazar.cheers.core.model.UserItem>?,
    friendRequests: List<com.salazar.cheers.core.model.UserItem>,
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
    user: com.salazar.cheers.core.model.UserItem,
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