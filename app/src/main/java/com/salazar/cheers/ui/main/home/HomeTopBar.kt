package com.salazar.cheers.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.ui.compose.DividerM3

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
            colors = TopAppBarDefaults.topAppBarColors(),
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
                IconButton(onClick = onActivityClick) {
                    BadgedBox(badge = {
                        if (notificationCount > 0)
                            Badge { Text(text = notificationCount.toString()) }
                    }) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Search icon"
                        )
                    }
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        painter = rememberAsyncImagePainter(model = R.drawable.ic_search_icon),
                        contentDescription = "Search icon"
                    )
                }
                val unreadChatCount = uiState.unreadChatCounter

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    if (unreadChatCount > 0)
                        Badge(
                            modifier = Modifier.offset(y = (-14).dp, x = 14.dp),
                        ) {
                            Text(
                                text = unreadChatCount.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    IconButton(onClick = onChatClick) {
                        Icon(
                            painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                            contentDescription = null,
                        )
                    }
                }
            },
        )
        if (showDivider)
            DividerM3()
    }
}

