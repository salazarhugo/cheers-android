package com.salazar.cheers.ui.main.event.guestlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.compose.items.UserItem
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.compose.user.FollowButton
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.event.add.TopAppBar
import kotlinx.coroutines.launch

@Composable
fun GuestListScreen(
    uiState: GuestListUiState,
    onSwipeRefresh: () -> Unit,
    onUserClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = "Guest list", onDismiss = onDismiss) }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            Tabs(
                uiState = uiState,
                onUserClick = onUserClick,
            )
        }
    }
}

@Composable
fun Tabs(
    uiState: GuestListUiState,
    onUserClick: (String) -> Unit,
) {
    val interested =
        if (uiState.interested != null) "${uiState.interested.size} interested" else "Interested"
    val going = if (uiState.going != null) "${uiState.going.size} going" else "Interested"
    val pages = listOf(interested, going, "Invited")
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
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
//                    viewModel.toggle()
                },
            )
        }
    }
    HorizontalPager(
        count = pages.size,
        state = pagerState,
    ) { page ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (page) {
                0 -> InterestedList(
                    users = uiState.interested,
                    onUserClick = onUserClick,
                )
                1 -> InterestedList(
                    users = uiState.going,
                    onUserClick = onUserClick,
                )
            }
        }
    }
}

@Composable
fun InterestedList(
    users: List<User>?,
    onUserClick: (String) -> Unit,
) {
    if (users != null) {
        LazyColumn {
            items(users) { user ->
                UserItem(
                    user = user,
                    onClick = { onUserClick(user.username) },
                ) {
                    FollowButton(isFollowing = user.followBack, onClick = {})
                }
            }
        }
    }
}
