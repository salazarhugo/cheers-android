package com.salazar.cheers.feature.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersBadgeBox
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.home.home.HomeSelectedPage

@Composable
internal fun HomeTopBarActions(
    homeSelectedPage: HomeSelectedPage,
    notificationCount: Int,
    onActivityClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMapClick: () -> Unit,
    onMyPartiesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.animateContentSize(),
    ) {
        when (homeSelectedPage) {
            HomeSelectedPage.FRIENDS -> {
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
            }

            HomeSelectedPage.PARTIES -> {
                IconButton(onClick = onMyPartiesClick) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Calendar icon",
                    )
                }
            }
        }
        IconButton(onClick = onMapClick) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
            )
        }
    }
}


@ComponentPreviews
@Composable
private fun HomeTopBarActionsPreview() {
    CheersPreview {
        HomeTopBarActions(
            homeSelectedPage = HomeSelectedPage.FRIENDS,
            notificationCount = 0,
            onActivityClick = {},
            onMapClick = {},
            onCameraClick = {},
            onMyPartiesClick = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun HomeTopBarActionsPreview_Parties() {
    CheersPreview {
        HomeTopBarActions(
            homeSelectedPage = HomeSelectedPage.PARTIES,
            notificationCount = 0,
            onActivityClick = {},
            onMapClick = {},
            onCameraClick = {},
            onMyPartiesClick = {},
        )
    }
}
