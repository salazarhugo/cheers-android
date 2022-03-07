package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.components.PrettyImage
import com.salazar.cheers.components.post.PostHeader
import com.salazar.cheers.components.story.StoryProgressBar
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.internal.User
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onStoryClick: () -> Unit,
    onStoryOpen: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val stories = uiState.storiesFlow?.collectAsLazyPagingItems() ?: return
    val scope = rememberCoroutineScope()

    Column() {
        StoryCarousel(
            stories = stories.itemSnapshotList.items,
            pagerState = pagerState,
            onStoryClick = onStoryClick,
            onStoryOpen = onStoryOpen,
            onStoryFinish = {
                if (pagerState.currentPage + 1 >= stories.itemCount)
                    onNavigateBack()
                else
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
            },
        )
    }
}

@Composable
fun StoryCarousel(
    stories: List<Story>,
    pagerState: PagerState,
    onStoryClick: () -> Unit,
    onStoryFinish: () -> Unit,
    onStoryOpen: (String) -> Unit,
) {
    HorizontalPager(
        count = stories.size,
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { page ->
        LaunchedEffect(currentPage) {
            if (page == currentPage && !stories[page].story.seenBy.contains(FirebaseAuth.getInstance().currentUser?.uid))
                onStoryOpen(stories[page].story.id)
        }
        Column {
            Box(
                Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                PrettyImage(
                    data = stories[page].story.photos[0],
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(9 / 16f)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .clickable { onStoryClick() }
                )
                StoryHeader(
                    currentPage = currentPage,
                    page = page,
                    user = stories[page].author,
                    onStoryFinish = onStoryFinish,
                )
            }
            StoryFooter()
        }
    }
}

@Composable
fun StoryHeader(
    currentPage: Int,
    page: Int,
    user: User,
    onStoryFinish: () -> Unit
) {
    Column() {
        StoryProgressBar(
            steps = 1,
            currentStep = 1,
            paused = currentPage != page,
            onFinished = onStoryFinish,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        PostHeader(
            username = user.username,
            verified = user.verified,
            public = false,
            locationName = "",
            profilePictureUrl = user.profilePictureUrl,
            onHeaderClicked = {},
            onMoreClicked = {}
        )
    }
}

@Composable
fun StoryFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        StoryInputField(
            value = "",
            onInputChange = {},
        )
    }
}

@Composable
fun StoryInputField(
    value: String,
    onInputChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        onValueChange = { onInputChange(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Send message") },
    )
}
