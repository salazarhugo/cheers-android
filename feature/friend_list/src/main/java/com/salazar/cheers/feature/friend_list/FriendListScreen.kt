package com.salazar.cheers.feature.friend_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.cheersUserItemList
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import kotlinx.coroutines.launch


@Composable
fun FriendListScreen(
    uiState: FriendListUiState,
    modifier: Modifier = Modifier,
    onFriendListUIAction: (FriendListUIAction) -> Unit = {},
) {
    val state = rememberRefreshLayoutState()
    val scope  = rememberCoroutineScope()
    LaunchedEffect(uiState.isRefreshing) {
        if (!uiState.isRefreshing) {
            scope.launch {
                state.finishRefresh(true)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = "Friends",
                onBackPressed = { onFriendListUIAction(FriendListUIAction.OnBackPressed) },
            )
        }
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = { onFriendListUIAction(FriendListUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            FriendList(
                friends = uiState.friends,
                modifier = Modifier.fillMaxSize(),
                onUserClicked = { onFriendListUIAction(FriendListUIAction.OnUserClick(it)) },
                onStoryClick = {},
                onRemoveClick = {
                    onFriendListUIAction(FriendListUIAction.OnRemoveFriendClick(it))
                },
            )
        }
    }
}

@Composable
fun FriendList(
    friends: List<UserItem>?,
    modifier: Modifier = Modifier,
    onUserClicked: (username: String) -> Unit = {},
    onStoryClick: (username: String) -> Unit = {},
    onRemoveClick: (String) -> Unit = {},
) {
    if (friends == null) {
        LoadingScreen()
    } else
        LazyColumn(
            modifier = modifier,
        ) {
            items(
                items = friends,
                key = { it.id },
            ) { user ->
                UserItem(
                    userItem = user,
                    onClick = onUserClicked,
                    onStoryClick = onStoryClick,
                ) {
                    OutlinedButton(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(34.dp),
                        onClick = { onRemoveClick(user.id) }
                    ) {
                        Text(
                            text = "Remove",
                        )
                    }
                }
            }
        }
}

@ScreenPreviews
@Composable
private fun FriendListScreenPreview() {
    CheersPreview {
        FriendListScreen(
            uiState = FriendListUiState(
                friends = cheersUserItemList,
            ),
            modifier = Modifier,
        )
    }
}

