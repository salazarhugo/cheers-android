package com.salazar.cheers.feature.home.navigation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersBadgeBox
import com.salazar.cheers.feature.home.R

@Composable
fun HomeTopBar(
    uiState: HomeUiState,
    notificationCount: Int,
    onSearchClick: () -> Unit,
    onActivityClick: () -> Unit,
    onChatClick: () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val showDivider by remember {
        derivedStateOf {
            uiState.listState.firstVisibleItemIndex > 0
        }
    }
    val icon = when (isDarkTheme) {
        true -> R.drawable.ic_cheers_logo
        false -> R.drawable.ic_cheers_logo
    }

    val image = when (isDarkTheme) {
        true -> R.drawable.cheers_logo_white
        false -> R.drawable.cheers_logo
    }

    Column {
        TopAppBar(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            colors = TopAppBarDefaults.mediumTopAppBarColors(),
            navigationIcon = {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(34.dp),
                    contentDescription = null,
                )
            },
            title = {
                Image(
                    painter = painterResource(image),
                    modifier = Modifier
                        .height(34.dp),
                    contentDescription = null,
                )
            },
            actions = {
                CheersBadgeBox(count = notificationCount) {
                    IconButton(onClick = onActivityClick) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Notification icon"
                        )
                    }
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                }
                val unreadChatCount = uiState.unreadChatCounter

                CheersBadgeBox(count = unreadChatCount) {
                    IconButton(onClick = onChatClick) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                        )
                    }
                }
            },
        )
        if (showDivider)
            Divider()
    }
}

