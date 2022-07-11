package com.salazar.cheers.ui.main.story

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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.salazar.cheers.components.post.PostHeader
import com.salazar.cheers.components.story.StoryProgressBar
import com.salazar.cheers.components.utils.PrettyImage
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.User
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    onStoryOpen: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUserClick: (String) -> Unit,
    value: String,
    onFocusChange: (Boolean) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
    showInterstitialAd: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val stories = uiState.stories
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (stories != null && stories.isNotEmpty())
        StoryCarousel(
            stories = stories,
            pagerState = pagerState,
            onStoryOpen = onStoryOpen,
            onStoryFinish = {
                onNavigateBack()
            },
            onUserClick = onUserClick,
            value = value,
            onInputChange = onInputChange,
            onSendReaction = onSendReaction,
            onFocusChange = onFocusChange,
            showInterstitialAd = showInterstitialAd,
            onStoryUIAction = onStoryUIAction,
        )
    }
}

@Composable
fun StoryCarousel(
    stories: List<Story>,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    showInterstitialAd: () -> Unit,
    pagerState: PagerState,
    onStoryFinish: () -> Unit,
    onStoryOpen: (String) -> Unit,
    onUserClick: (String) -> Unit,
    value: String,
    onFocusChange: (Boolean) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
) {
    HorizontalPager(
        count = 1,
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->

        var isPaused by remember { mutableStateOf(false) }
        val isPressed = remember { mutableStateOf(false) }
        var currentStep by remember { mutableStateOf(0) }
        val story = stories[currentStep]


        LaunchedEffect(currentPage) {
            if (page == currentPage && !story.seen)
                onStoryOpen(story.id)
        }

        if ((page - 1) % 3 == 0) {
            showInterstitialAd()
        }

        Scaffold(
            bottomBar = {
                StoryFooter(
                    story = story,
                    value = value,
                    isUserMe = story.authorId == FirebaseAuth.getInstance().currentUser?.uid!!,
                    onInputChange = onInputChange,
                    onSendReaction = { onSendReaction(story, it) },
                    onFocusChange = {
                        isPaused = it
                                    },
                    onStoryUIAction = onStoryUIAction,
                )
            },
            containerColor = Color.Black,
            modifier = Modifier
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
                data = story.photoUrl,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(it)
                    .aspectRatio(9 / 16f)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        val maxWidth = this.size.width
                        detectTapGestures(
                            onPress = {
                                val pressStartTime = System.currentTimeMillis()
                                isPressed.value = true
                                this.tryAwaitRelease()
                                val pressEndTime = System.currentTimeMillis()
                                val totalPressTime = pressEndTime - pressStartTime
                                if (totalPressTime < 200) {
                                    val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                                    if (isTapOnRightThreeQuarters) {
                                        if(currentStep + 1 >= stories.size)
                                            onStoryFinish()
                                        else
                                            currentStep++
                                    }
                                    else
                                        currentStep = (currentStep - 1).coerceAtLeast(0)
                                }
                                isPressed.value = false
                            },
                        )
                    }
//                    .clickable {
//                        onStoryUIAction(StoryUIAction.OnTap, story.id)
//                    }
            )

            StoryHeader(
                story = story,
                count = stories.size,
                onStoryFinish = {
                    if(currentStep + 1 >= stories.size)
                        onStoryFinish()
                    else
                        currentStep++
                },
                onUserClick = onUserClick,
                pause = false,
                currentStep = currentStep,
            )
        }
    }
}

@Composable
fun StoryHeader(
    story: Story,
    count: Int,
    currentStep: Int,
    onStoryFinish: () -> Unit,
    onUserClick: (String) -> Unit,
    pause: Boolean,
) {
    Column {
        StoryProgressBar(
            steps = count,
            currentStep = currentStep,
            paused = pause,
            onFinished = onStoryFinish,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        PostHeader(
            username = story.username,
            verified = story.verified,
            created = story.created,
            beverage = Beverage.NONE,
            darkMode = true,
            public = false,
            locationName = "",
            profilePictureUrl = story.profilePictureUrl,
            onHeaderClicked = onUserClick,
            onMoreClicked = {},
        )
    }
}

@Composable
fun StoryFooter(
    story: Story,
    value: String,
    isUserMe: Boolean,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
) {
    if (isUserMe)
        StoryMeFooter(
            story = story,
            onStoryUIAction = onStoryUIAction,
        )
    else
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(16.dp)
        ) {
            StoryInputField(
                value = value,
                onInputChange = onInputChange,
                onSendReaction = onSendReaction,
                onFocusChange = onFocusChange
            )
        }
}

@Composable
fun StoryMeFooter(
    story: Story,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                onStoryUIAction(StoryUIAction.OnActivity, story.id)
            }
        ) {
            Icon(
                Icons.Outlined.AccountCircle,
                contentDescription = null,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Activity",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                onStoryUIAction(StoryUIAction.OnDelete, story.id)
            }
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null, tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Delete",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun StoryInputField(
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
