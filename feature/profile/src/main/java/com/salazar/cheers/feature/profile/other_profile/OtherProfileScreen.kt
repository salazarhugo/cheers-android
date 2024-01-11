package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.RequestFriendButton
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.feature.profile.profile.ProfileUIAction
import kotlinx.coroutines.launch

@Composable
fun OtherProfileScreen(
    uiState: OtherProfileUiState.HasUser,
    onOtherProfileUIAction: (OtherProfileUIAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val posts = uiState.posts
    val parties = uiState.parties
    val user = uiState.user
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
        topBar = {
            OtherProfileTopBar(
                username = user.username,
                verified = user.verified,
                onBackPressed = { onOtherProfileUIAction(OtherProfileUIAction.OnBackPressed) },
                onCopyUrl = {},
                onManageFriendship = {
                    onOtherProfileUIAction(OtherProfileUIAction.OnFriendshipClick)
                },
            )
        }
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = { onOtherProfileUIAction(OtherProfileUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            OtherProfileList(
                user = user,
                state = listState,
                posts = posts,
                parties = parties,
                onOtherProfileUIAction = onOtherProfileUIAction,
            )
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
    onMessageClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (friend) {
            CheersOutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onMessageClick,
            ) {
                Text("Message")
            }
        } else {
            RequestFriendButton(
                requested = requested,
                hasRequestedViewer = hasRequestedViewer,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                onCancelFriendRequest = onCancelFriendRequest,
                onSendFriendRequest = onSendFriendRequest,
                onAcceptFriendRequest = onAcceptFriendRequest,
            )
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
