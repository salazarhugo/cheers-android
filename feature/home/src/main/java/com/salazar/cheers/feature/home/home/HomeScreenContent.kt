package com.salazar.cheers.feature.home.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeScreenContent(
    pagerState: PagerState,
//    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier,
    friendsTabContent: @Composable () -> Unit,
    partiesTabContent: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
//        modifier = Modifier.pullRefresh(pullRefreshState)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .let { if (isRefreshing) it.padding(top = 16.dp) else it } //add a padding to refresh indicator
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = true,
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> friendsTabContent()
                    1 -> partiesTabContent()
                }
            }
        }

//        PullRefreshIndicator(
//            refreshing = isRefreshing,
//            state = pullRefreshState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = toolBarHeight)
//        )
    }
}
