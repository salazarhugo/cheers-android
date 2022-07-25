package com.salazar.cheers.ui.main.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.salazar.cheers.components.EmptyActivity
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.components.user.FollowButton
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.internal.toSentence
import com.salazar.cheers.ui.main.add.ProfilePicture
import com.salazar.cheers.ui.main.event.add.TopAppBar

@Composable
fun ActivityScreen(
    uiState: ActivityUiState,
    onBackNav: () -> Unit,
    onSwipeRefresh: () -> Unit,
    onActivityClick: (Activity) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onDismiss = onBackNav,
                title = "Activity"
            )
        }
    ) {
        if (uiState.isLoading)
            LoadingScreen()
        else
            SwipeToRefresh(
                state = rememberSwipeToRefreshState(isRefreshing = false),
                onRefresh = onSwipeRefresh,
                modifier = Modifier.padding(it),
            ) {
                Column {
                    val activities = uiState.activities

                    if (activities?.isEmpty() == true)
                        EmptyActivity()
                    else
                        ActivityList(
                            activities = activities,
                            onActivityClick = onActivityClick,
                        )
                }
            }
    }
}


@Composable
fun ActivityList(
    activities: List<Activity>?,
    onActivityClick: (Activity) -> Unit,
) {
    LazyColumn {
        item {
            Text(
                text = "This week",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
        if (activities != null)
            items(activities) {
                ActivityItem(activity = it, onActivityClick)
            }
    }
}

@Composable
fun ActivityItem(
    activity: Activity,
    onActivityClick: (Activity) -> Unit,
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
            ProfilePicture(profilePictureUrl = activity.avatar)
            Spacer(modifier = Modifier.width(16.dp))
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(activity.username)
                }
                append(" ")
                append(activity.type.toSentence())
                append(" ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                    append(relativeTimeFormatter(timestamp = activity.time))
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true,
            )
        }
        if (activity.type == ActivityType.FOLLOW)
            FollowButton(
                isFollowing = true,
                onClick = {},
            )
        else if (activity.type == ActivityType.POST_LIKE)
            Image(
                modifier = Modifier
                    .size(50.dp),
                painter = rememberAsyncImagePainter(model = activity.photoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
    }
}
