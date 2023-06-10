package com.salazar.cheers.ui.main.story.stats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.user.User
import com.salazar.cheers.feature.search.UserCard
import com.salazar.cheers.ui.compose.DividerM3
import kotlin.math.absoluteValue

@Composable
fun StoryStatsScreen(
    uiState: StoryStatsViewModelUiState,
    onUserClick: (String) -> Unit,
    onDeleteStory: (String) -> Unit,
    onCloseClick: () -> Unit,
) {

    val stories = uiState.stories
    val pagerState = rememberPagerState()

    if (stories != null) {
        Column {
            StoryStatsToolbar(onCloseClick = onCloseClick) {
            }
            HorizontalPager(
                count = stories.size,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 32.dp),
                modifier = Modifier.weight(0.3f)
            ) { page ->
                StoryCard(stories[page], this, page)
            }
            val story = stories[pagerState.currentPage]
            Views(
                views = 999,//story.story.seenBy.size,
                modifier = Modifier.weight(0.7f),
                viewers = listOf(
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                    User(),
                ),
                onUserClick = onUserClick,
                onDeleteStory = { onDeleteStory(story.id) },
            )
        }
    } else
        com.salazar.cheers.core.share.ui.LoadingScreen()
}

@Composable
fun StoryStatsToolbar(
    onCloseClick: () -> Unit,
    onStorySettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onStorySettingsClick) {
            Icon(Icons.Outlined.Settings, contentDescription = null)
        }
        IconButton(onClick = onCloseClick) {
            Icon(Icons.Outlined.Close, contentDescription = null)
        }
    }
}

@Composable
fun ViewsHeader(
    onDeleteStory: () -> Unit,
    views: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Visibility, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            AnimatedTextCounter(
                targetState = views,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Outlined.Download, contentDescription = null)
            Icon(
                Icons.Outlined.Delete,
                modifier = Modifier.clickable { onDeleteStory() },
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
    }
}

@Composable
fun Views(
    views: Int,
    viewers: List<User>,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit,
    onDeleteStory: () -> Unit
) {
    Column(
        modifier = modifier.background(Color.Black),
    ) {
        ViewsHeader(
            views = views,
            onDeleteStory = onDeleteStory,
        )

        DividerM3()

        LazyColumn(
            modifier = Modifier.animateContentSize()
        ) {
            item {
                Text(
                    text = "Viewers",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(16.dp),
                )
            }

            items(viewers) { user ->
                UserCard(user = user, onUserClicked = {})
            }
        }
    }
}

@Composable
fun StoryCard(
    suggestedUser: Story,
    scope: PagerScope,
    page: Int
) {
    scope.apply {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp,
            tonalElevation = 8.dp,
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    // We animate the scaleX + scaleY, between 85% and 100%
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                },
        ) {
            Box(
                modifier = Modifier.clickable {
                }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        suggestedUser.photo
                    ),
                    modifier = Modifier
                        .aspectRatio(9 / 16.0f),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
