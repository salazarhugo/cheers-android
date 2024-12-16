package com.salazar.cheers.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersBadgeBox
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
    unreadChatCount: Int,
    collapsedFraction: Float,
    notificationCount: Int,
    onSearchClick: () -> Unit,
    onActivityClick: () -> Unit,
    onChatClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCityClick: () -> Unit,
    onMapClick: () -> Unit,
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
                IconButton(onClick = onCameraClick) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Notification icon"
                    )
                }
                CheersBadgeBox(count = notificationCount) {
                    IconButton(onClick = onActivityClick) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Notification icon"
                        )
                    }
                }

//                CheersBadgeBox(count = unreadChatCount) {
//                    IconButton(onClick = onChatClick) {
//                        Icon(
//                            imageVector = Icons.Outlined.ChatBubbleOutline,
//                            contentDescription = null,
//                        )
//                    }
//                }
                IconButton(onClick = onMapClick) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = null,
                    )
                }
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
            unreadChatCount = 2,
            collapsedFraction = 0f,
            notificationCount = 0,
            onSearchClick = {},
            onChatClick = {},
            onActivityClick = {},
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            onCityClick = {},
            onMapClick = {},
            onCameraClick = {},
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
            unreadChatCount = 3,
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            collapsedFraction = 1f,
            notificationCount = 0,
            onSearchClick = {},
            onChatClick = {},
            onActivityClick = {},
            onCityClick = {},
            onMapClick = {},
            onCameraClick = {},
        )
    }
}
