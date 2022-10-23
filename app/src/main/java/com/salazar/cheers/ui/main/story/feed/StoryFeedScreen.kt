package com.salazar.cheers.ui.main.story.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.post.PostHeader
import com.salazar.cheers.compose.story.StoryProgressBar
import com.salazar.cheers.compose.utils.PrettyImage
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.carousel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun StoryFeedScreen(
    uiState: StoryFeedUiState,
    onStoryFeedUIAction: (StoryFeedUIAction) -> Unit,
) {
    val usersWithStories = uiState.usersWithStories

    if (usersWithStories == null)
        LoadingScreen()
    else
        StoryFeedCarousel(
            initialPage = uiState.page,
            usersWithStories = usersWithStories,
            onStoryFeedUIAction = onStoryFeedUIAction,
        )
}

@Composable
fun StoryFeedCarousel(
    initialPage: Int,
    usersWithStories: List<UserWithStories>,
    onStoryFeedUIAction: (StoryFeedUIAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = initialPage)
    val currentPage = pagerState.currentPage

    HorizontalPager(
        count = usersWithStories.size,
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->
        val userWithStories = usersWithStories[page]
        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

        UserWithStories(
            isCurrentPage = currentPage == page,
            modifier = Modifier
                .background(Color.Black)
                .carousel(pageOffset),
            userWithStories = userWithStories,
            onStoryFeedUIAction = onStoryFeedUIAction,
            onNextPage = {
                if (page == pagerState.pageCount - 1)
                    onStoryFeedUIAction(StoryFeedUIAction.OnBackPressed)
                else
                    scope.launch {
                        pagerState.nextPage()
                    }
            },
            onPreviousPage = {
                if (page == 0)
                    onStoryFeedUIAction(StoryFeedUIAction.OnBackPressed)
                else
                    scope.launch {
                        pagerState.previousPage()
                    }
            }
        )
    }
}

suspend fun PagerState.nextPage() {
    animateScrollToPage(currentPage + 1)
}

suspend fun PagerState.previousPage() {
    animateScrollToPage(currentPage - 1)
}

@Composable
fun UserWithStories(
    modifier: Modifier = Modifier,
    isCurrentPage: Boolean,
    userWithStories: UserWithStories,
    onStoryFeedUIAction: (StoryFeedUIAction) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
) {
    val stories = userWithStories.stories
    val scope = rememberCoroutineScope()
    val stepperState = rememberPagerState()
    val currentStep = stepperState.currentPage
    var paused by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            StoryFeedHeader(
                user = userWithStories.user,
                story = stories[currentStep],
                count = stories.size,
                onStepFinish = { last ->
                    if (last)
                        onNextPage()
                    else
                        scope.launch {
                            stepperState.nextPage()
                        }
                },
                onUserClick = { onStoryFeedUIAction(StoryFeedUIAction.OnUserClick(userWithStories.user.id))},
                pause = !isCurrentPage || paused,
                currentStep = currentStep,
            )
        },
        bottomBar = {
            StoryFeedFooter(
                messageInput = "",
                isUserMe = false,
                onStoryFeedUIAction = onStoryFeedUIAction,
            )
        },
        containerColor = Color.Black,
    ) {
        HorizontalPager(
            modifier = Modifier.padding(it),
            count = stories.size,
            state = stepperState,
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false,
        ) { step ->
            val story = stories[step]

            StoryPage(
                story = story,
                onPauseChange = {
                    paused = it
                },
                onNextStep = {
                    if (step == stepperState.pageCount - 1)
                        onNextPage()
                    else
                        scope.launch {
                            stepperState.nextPage()
                        }
                },
                onPreviousStep = {
                    if (step == 0)
                        onPreviousPage()
                    else
                        scope.launch {
                            stepperState.previousPage()
                        }
                },
                onViewed = {
                    onStoryFeedUIAction(StoryFeedUIAction.OnViewed(it))
                }
            )
        }
    }
}

@Composable
fun StoryPage(
    story: Story,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onPauseChange: (Boolean) -> Unit,
    onViewed: (String) -> Unit
) {
    // Mark the story as viewed
    LaunchedEffect(Unit) {
        onViewed(story.id)
    }

    PrettyImage(
        data = story.photo,
        contentDescription = null,
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(9 / 16f)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .pointerInput(Unit) {
                val maxWidth = this.size.width
                detectTapGestures(
                    onPress = {
                        val pressStartTime = System.currentTimeMillis()

                        onPauseChange(true)
                        val wasConsumedByOtherGesture = !tryAwaitRelease()
                        onPauseChange(false)
                        if (wasConsumedByOtherGesture) return@detectTapGestures

                        val pressEndTime = System.currentTimeMillis()
                        val totalPressTime = pressEndTime - pressStartTime

                        if (totalPressTime > 200) return@detectTapGestures

                        val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                        if (isTapOnRightThreeQuarters)
                            onNextStep()
                        else
                            onPreviousStep()
                    },
                )
            }
    )
}

@Composable
fun StoryFeedHeader(
    user: User,
    story: Story,
    count: Int,
    currentStep: Int,
    onStepFinish: (Boolean) -> Unit,
    onUserClick: (String) -> Unit,
    pause: Boolean,
) {
    Column {
        StoryProgressBar(
            steps = count,
            currentStep = currentStep,
            paused = pause,
            onStepFinish = onStepFinish,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        PostHeader(
            username = user.username,
            verified = user.verified,
            created = story.created,
            beverage = Beverage.NONE,
            darkMode = true,
            public = false,
            locationName = "",
            picture = user.picture,
            onHeaderClicked = onUserClick,
            onMoreClicked = {},
        )
    }
}

@Composable
fun StoryFeedFooter(
    messageInput: String,
    isUserMe: Boolean,
    onStoryFeedUIAction: (StoryFeedUIAction) -> Unit,
) {
    if (isUserMe)
        StoryFeedMeFooter(
            onStoryFeedUIAction = onStoryFeedUIAction,
        )
    else
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(16.dp)
        ) {
            StoryFeedInputField(
                value = messageInput,
                onInputChange = {},
                onSendReaction = {},
                onFocusChange = {},
            )
        }
}

@Composable
fun StoryFeedMeFooter(
    onStoryFeedUIAction: (StoryFeedUIAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Item(
            text = "Activity",
            icon = Icons.Outlined.AccountCircle,
            onClick = {
                onStoryFeedUIAction(StoryFeedUIAction.OnActivity)
            },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Item(
                text = "Share",
                icon = Icons.Default.Share,
                onClick = {},
            )
            Item(
                text = "More",
                icon = Icons.Default.MoreVert,
                onClick = {
                    onStoryFeedUIAction(StoryFeedUIAction.OnMore)
                },
            )
        }
    }
}

@Composable
fun Item(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
        )
    }
}

@Composable
fun StoryFeedInputField(
    value: String,
    onInputChange: (String) -> Unit,
    onSendReaction: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.White, CircleShape)
            .onFocusChanged {
                onFocusChange(it.hasFocus)
            },
        onValueChange = { onInputChange(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onSend = {
            onSendReaction(value)
            focusManager.clearFocus()
        }),
        trailingIcon = {
            if (value.isNotBlank())
                Icon(
                    Icons.Default.Send,
                    modifier = Modifier.clickable {
                        onSendReaction(value)
                        focusManager.clearFocus()
                    },
                    contentDescription = null,
                    tint = Color.White,
                )
        },
        placeholder = {
            Text(
                text = "Send message",
                color = Color.White
            )
        },
    )
}
