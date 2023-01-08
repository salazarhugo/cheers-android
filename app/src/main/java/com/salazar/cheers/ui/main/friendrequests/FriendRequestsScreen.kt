package com.salazar.cheers.ui.main.friendrequests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.salazar.cheers.ui.compose.user.FollowButton
import com.salazar.cheers.ui.main.party.create.TopAppBar
import kotlinx.serialization.json.JsonNull.content

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
                        friendRequests = friendRequests,
                        onFriendRequestsUIAction = onFriendRequestsUIAction,
                    )
            }
    }
}

@Composable
fun FriendRequestList(
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
        Button(
            modifier = Modifier.height(34.dp),
            onClick = {
                onFriendRequestsUIAction(FriendRequestsUIAction.OnAcceptFriendRequest(user.id))
            },
        ) {
            Text("Accept")
        }
        CheersOutlinedButton(
            onClick = {},
        ) {
            Text("Cancel")
        }
    }
}