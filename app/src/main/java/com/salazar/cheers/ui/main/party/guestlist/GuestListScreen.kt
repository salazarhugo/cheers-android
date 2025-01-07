package com.salazar.cheers.ui.main.party.guestlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.UserItemListLoading
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import kotlinx.coroutines.launch

@Composable
fun GuestListScreen(
    uiState: GuestListUiState,
    goingState: GuestListGoingState,
    interestedState: GuestListGoingState,
    onSwipeRefresh: () -> Unit,
    onUserClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Guest list",
                onBackPressed = onDismiss,
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isRefreshing),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            Tabs(
                onUserClick = onUserClick,
                goingState = goingState,
                interestedState = interestedState,
            )
        }
    }
}

@Composable
fun Tabs(
    goingState: GuestListGoingState,
    interestedState: GuestListGoingState,
    onUserClick: (String) -> Unit,
) {
    val interested =
        "${(interestedState as? GuestListGoingState.Users)?.users?.size ?: ""} interested"
    val going =
        "${(goingState as? GuestListGoingState.Users)?.users?.size ?: ""} going"

    val pages = listOf(interested, going, "Invited")
    val pagerState = rememberPagerState {
        pages.size
    }
    val scope = rememberCoroutineScope()

    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(title, style = MaterialTheme.typography.bodyMedium) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                },
            )
        }
    }
    HorizontalPager(state = pagerState) { page ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (page) {
                0 -> InterestedList(
                    state = interestedState,
                    onUserClick = onUserClick,
                )

                1 -> InterestedList(
                    state = goingState,
                    onUserClick = onUserClick,
                )
            }
        }
    }
}

@Composable
fun InterestedList(
    state: GuestListGoingState,
    onUserClick: (String) -> Unit,
) {
    LazyColumn {
        when (state) {
            GuestListGoingState.Loading -> {
                item {
                    UserItemListLoading()
                }
            }

            is GuestListGoingState.Users -> {
                val users = state.users

                items(
                    items = users,
                ) { user ->
                    UserItem(
                        userItem = user,
                        onClick = { onUserClick(user.username) },
                    ) {
                        FriendButton(
                            isFriend = user.friend,
                            onClick = {},
                        )
                    }
                }
            }
        }

    }
}
