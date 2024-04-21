package com.salazar.cheers.feature.notifications.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.ui.CheersBadgeBox
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.messageFormatter
import com.salazar.cheers.core.ui.text.MyText
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.core.ui.AddFriendButton
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.util.relativeTimeFormatter
import com.salazar.cheers.core.model.ActivityType


@Composable
fun ActivityScreen(
    uiState: ActivityUiState,
    onActivityUIAction: (ActivityUIAction) -> Unit,
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

                ActivityList(
                    uiState = uiState,
                    activities = activities,
                    onActivityUIAction = onActivityUIAction,
                )
            }
        }
    }
}


@Composable
fun ActivityList(
    uiState: ActivityUiState,
    activities: List<Activity>?,
    onActivityUIAction: (ActivityUIAction) -> Unit,
) {
    val suggestions = uiState.suggestions
    LazyColumn {
        item {
            FriendRequests(
                count = uiState.friendRequestCounter,
                picture = uiState.friendRequestPicture,
                onClick = { onActivityUIAction(ActivityUIAction.OnFriendRequestsClick) },
            )
        }
        if (!suggestions.isNullOrEmpty()) {
            item {
                MyText(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
            items(items = suggestions) { user ->
                UserItem(
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
            items(activities, key = { it.id }) {
                ActivityItem(
                    activity = it,
                    onActivityClick = { onActivityUIAction(ActivityUIAction.OnActivityClick(it)) },
                    onActivityUIAction = onActivityUIAction,
                )
            }
        }
    }
}

@Composable
fun FriendRequests(
    count: Int? = null,
    picture: String? = null,
    onClick: () -> Unit,
) {
    if (count == null || count == 0)
        return

    Row(
        modifier = Modifier
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

@Composable
fun ActivityItem(
    activity: Activity,
    onActivityClick: (Activity) -> Unit,
    onActivityUIAction: (ActivityUIAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onActivityClick(activity) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarComponent(
                avatar = activity.avatar,
                size = 46.dp,
                onClick = {
                    onActivityUIAction(ActivityUIAction.OnUserClick(userId = activity.username))
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    val text = messageFormatter(text = activity.text, primary = true)
                    append(text)
                }
                append(" ")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ), fontWeight = FontWeight.Normal
                    )
                ) {
                    append(relativeTimeFormatter(seconds = activity.createTime))
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true,
            )
        }
        if (activity.type == ActivityType.FRIEND_ADDED)
            FriendButton(
                modifier = Modifier.padding(start = 16.dp),
                isFriend = true,
                onClick = {},
            )
        if (activity.photoUrl.isNotBlank()) {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onActivityUIAction(ActivityUIAction.OnPostClick(activity.mediaId)) }
                    .size(50.dp),
                painter = rememberAsyncImagePainter(model = activity.photoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}
