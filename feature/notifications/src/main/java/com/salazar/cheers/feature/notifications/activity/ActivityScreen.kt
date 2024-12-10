package com.salazar.cheers.feature.notifications.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.ui.AddFriendButton
import com.salazar.cheers.core.ui.CheersBadgeBox
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.components.filters.Filters
import com.salazar.cheers.core.ui.text.MyText
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState


@Composable
fun ActivityScreen(
    uiState: ActivityUiState,
    onActivityUIAction: (ActivityUIAction) -> Unit,
    onFilterClick: (Filter) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Activity",
                onBackPressed = { onActivityUIAction(ActivityUIAction.OnBackPressed) },
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = { onActivityUIAction(ActivityUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            Column {
                val activities = uiState.activities
                val filters = uiState.filters

                ActivityList(
                    uiState = uiState,
                    activities = activities,
                    filters = filters,
                    onActivityUIAction = onActivityUIAction,
                    onFilterClick = onFilterClick,
                )
            }
        }
    }
}


@Composable
fun ActivityList(
    uiState: ActivityUiState,
    activities: List<Activity>?,
    filters: List<Filter>?,
    onActivityUIAction: (ActivityUIAction) -> Unit,
    onFilterClick: (Filter) -> Unit,
) {
    val suggestions = uiState.suggestions

    LazyColumn {
        filters(
            filters = filters,
            onFilterClick = onFilterClick,
        )

        friendRequests(
            count = uiState.friendRequestCounter,
            picture = uiState.friendRequestPicture,
            onClick = { onActivityUIAction(ActivityUIAction.OnFriendRequestsClick) },
        )

        if (!suggestions.isNullOrEmpty()) {
            item {
                MyText(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .animateItem()
                        .padding(16.dp),
                )
            }
            items(items = suggestions) { user ->
                UserItem(
                    modifier = Modifier.animateItem(),
                    userItem = user,
                    onClick = {
                        onActivityUIAction(ActivityUIAction.OnUserClick(user.username))
                    },
                    content = {
                        AddFriendButton(
                            requestedByViewer = user.requested,
                            onAddFriendClick = {
                                onActivityUIAction(ActivityUIAction.OnAddFriendClick(user.id))
                            },
                            onCancelFriendRequestClick = {
                                onActivityUIAction(ActivityUIAction.OnCancelFriendRequestClick(user.id))
                            },
                            onDelete = {
                                onActivityUIAction(ActivityUIAction.OnRemoveSuggestion(user))
                            }
                        )
                    }
                )
            }
        }
        if (!activities.isNullOrEmpty()) {
            item {
                MyText(
                    text = "This week",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
            items(
                items = activities,
                key = { it.id },
            ) {
                ActivityItem(
                    activity = it,
                    modifier = Modifier.animateItem(),
                    onActivityClick = { onActivityUIAction(ActivityUIAction.OnActivityClick(it)) },
                    onActivityUIAction = onActivityUIAction,
                )
            }
        }
    }
}

private fun LazyListScope.filters(
    filters: List<Filter>? = null,
    onFilterClick: (Filter) -> Unit,
) {
    if (filters == null) {
        return
    }

    item {
        Filters(
            filters = filters,
            onFilterClick = onFilterClick,
        )
    }
}

private fun LazyListScope.friendRequests(
    count: Int? = null,
    picture: String? = null,
    onClick: () -> Unit,
) {
    if (count == null || count == 0) {
        return
    }

    item {
        Row(
            modifier = Modifier
                .animateItem()
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheersBadgeBox(count = count) {
                    AvatarComponent(
                        avatar = picture,
                        size = 40.dp,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Friend requests",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Approve or ignore requests",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
                    )
                }
            }
        }
    }
}