package com.salazar.cheers.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.location.LocationComponent
import com.salazar.cheers.feature.home.R
import com.salazar.cheers.feature.home.home.HomeScreenHeader
import com.salazar.cheers.feature.home.home.HomeSelectedPage

@Composable
internal fun HomeTopBar(
    currentCity: String,
    pagerState: PagerState,
    homeSelectedPage: HomeSelectedPage,
    collapsedFraction: Float,
    notificationCount: Int,
    onActivityClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCityClick: () -> Unit,
    onMapClick: () -> Unit,
    onMyPartiesClick: () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val icon = when (isDarkTheme) {
        true -> R.drawable.ic_cheers_logo
        false -> R.drawable.ic_cheers_logo
    }

    val image = when (isDarkTheme) {
        true -> R.drawable.cheers_logo_white
        false -> R.drawable.cheers_logo
    }

    val isCollapsed = collapsedFraction == 1F

    Column {
        TopAppBar(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            colors = TopAppBarDefaults.mediumTopAppBarColors(),
            title = {
                AnimatedVisibility(
                    visible = !isCollapsed,// && currentCity.isNotBlank(),
                ) {
                    LocationComponent(
                        city = currentCity,
                        onClick = onCityClick,
                    )
                }
                AnimatedVisibility(
                    visible = isCollapsed,
                ) {
                    Image(
                        painter = painterResource(image),
                        modifier = Modifier
                            .height(34.dp),
                        contentDescription = "Cheers image",
                    )
                }
            },
            actions = {
                HomeTopBarActions(
                    homeSelectedPage = homeSelectedPage,
                    onCameraClick = onCameraClick,
                    onMapClick = onMapClick,
                    onActivityClick = onActivityClick,
                    onMyPartiesClick = onMyPartiesClick,
                    notificationCount = notificationCount,
                )
            },
        )
        HomeScreenHeader(
            modifier = Modifier.padding(horizontal = 8.dp),
            homeSelectedPage = homeSelectedPage,
            collapsedFraction = collapsedFraction,
            pagerState = pagerState,
        )
        if (isCollapsed) {
            HorizontalDivider()
        }
    }
}


@ComponentPreviews
@Composable
private fun HomeTopBarPreview() {
    CheersPreview {
        HomeTopBar(
            currentCity = "Paris",
            pagerState = rememberPagerState { 2 },
            collapsedFraction = 0f,
            notificationCount = 0,
            onActivityClick = {},
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            onCityClick = {},
            onMapClick = {},
            onCameraClick = {},
            onMyPartiesClick = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun HomeTopBarCollapsedPreview() {
    CheersPreview {
        HomeTopBar(
            currentCity = "Paris",
            pagerState = rememberPagerState { 2 },
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            collapsedFraction = 1f,
            notificationCount = 0,
            onActivityClick = {},
            onCityClick = {},
            onMapClick = {},
            onCameraClick = {},
            onMyPartiesClick = {},
        )
    }
}
