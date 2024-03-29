package com.salazar.cheers.feature.post_likes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.cheersUserItemList
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.theme.Roboto
import kotlinx.coroutines.launch

@Composable
fun PostLikesScreen(
    uiState: PostLikesUiState,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onPullRefresh: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
) {
    val state = rememberRefreshLayoutState()
    val scope  = rememberCoroutineScope()
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            scope.launch {
                state.finishRefresh(true)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { Toolbar(navigateBack = onBackPressed) }
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = onPullRefresh,
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            }
            val users = uiState.users
            if (users != null) {
                UserList(
                    modifier = Modifier.fillMaxSize(),
                    users = users,
                    onUserClick = onUserClick,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun PostLikesScreenPreview() {
    CheersPreview {
        PostLikesScreen(
            uiState = PostLikesUiState(
                users = cheersUserItemList,
            ),
            modifier = Modifier,
        )
    }
}

@Composable
fun Toolbar(
    navigateBack: () -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Likes",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                )
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Outlined.ArrowBack, "")
                }
            })
    }
}

@Composable
fun UserList(
    users: List<UserItem>,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = users,
            key = { it.id },
        ) { user ->
            UserItem(
                userItem = user,
                onClick = onUserClick,
            ) {
                FriendButton(
                    isFriend = user.friend,
                    requested = user.requested,
                    onClick = { /*TODO*/ }
                )
            }
        }
    }
}