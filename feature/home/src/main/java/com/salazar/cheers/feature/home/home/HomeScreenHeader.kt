package com.salazar.cheers.feature.home.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun HomeScreenHeader(
    homeSelectedPage: HomeSelectedPage,
    pagerState: PagerState,
    collapsedFraction: Float,
    modifier: Modifier = Modifier,
) {
    val animatedButtonColor = animateColorAsState(
        targetValue = when (homeSelectedPage) {
            HomeSelectedPage.FRIENDS -> MaterialTheme.colorScheme.primary
            HomeSelectedPage.PARTIES -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        ),
        label = "selectedHomePageBackgroundAnimation"
    )

    // Tab layout
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    ) {
        HomePageTabLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            tabs = HomeSelectedPage.entries.toTypedArray().map { it.page },
            pagerState = pagerState,
        )
    }
}


@ComponentPreviews
@Composable
private fun HomeScreenHeaderPreview() {
    CheersPreview {
        HomeScreenHeader(
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            collapsedFraction = 1f,
            pagerState = rememberPagerState { 2 },
        )
    }
}