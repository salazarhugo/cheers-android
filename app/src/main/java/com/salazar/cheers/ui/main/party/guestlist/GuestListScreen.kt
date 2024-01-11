package com.salazar.cheers.ui.main.party.guestlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.ui.main.party.create.TopAppBar
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
            state = rememberSwipeToRefreshState(uiState.isLoading),
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
    val pagerState = rememberPagerState {
        pages.size
    }
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
            )
        },
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
    HorizontalPager(state = pagerState) { page ->
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
    users: List<UserItem>?,
    onUserClick: (String) -> Unit,
) {
    if (users != null) {
        LazyColumn {
            items(users) { user ->
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
