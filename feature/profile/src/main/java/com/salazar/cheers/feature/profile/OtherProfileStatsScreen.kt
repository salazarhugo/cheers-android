package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import kotlinx.coroutines.launch

@Composable
fun OtherProfileStatsScreen(
    uiState: OtherProfileStatsUiState,
    onSwipeRefresh: () -> Unit,
    onBackPressed: () -> Unit,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                username = uiState.username,
                verified = uiState.verified,
                onBackPressed = onBackPressed
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            Column {
                Tabs(
                    uiState = uiState,
                    onUserClicked = onUserClicked,
                    onFollowToggle = onFollowToggle,
                    onStoryClick = onStoryClick,
                )
            }
        }
    }
}

@Composable
fun Tabs(
    uiState: OtherProfileStatsUiState,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    val friendsTitle =
        if (uiState.friends == null)
            "Friends"
        else
            "${uiState.friends.size} friends"

    val pages = listOf(friendsTitle)

    val pagerState = rememberPagerState(
        pageCount = { pages.size },
    )
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
//            TabRowDefaults.Indicator(
//                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
//            )
        },
//        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                },
            )
        }
    }
//    SearchBar()
    HorizontalPager(
        state = pagerState,
    ) { page ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (page) {
                0 -> Following(
                    following = uiState.friends,
                    onUserClicked = onUserClicked,
                    onStoryClick = onStoryClick,
                    onFollowToggle = onFollowToggle,
                )
            }
        }
    }
}