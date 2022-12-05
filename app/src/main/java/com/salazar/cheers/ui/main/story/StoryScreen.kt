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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.compose.post.PostHeader
import com.salazar.cheers.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.compose.story.StoryProgressBar
import com.salazar.cheers.compose.utils.PrettyImage
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.ui.carousel
import kotlin.math.absoluteValue


@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    onStoryOpen: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUserClick: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
    showInterstitialAd: () -> Unit,
    onPauseChange: (Boolean) -> Unit,
    onCurrentStepChange: (Int) -> Unit,
) {
    val pagerState = rememberPagerState()
    val stories = uiState.stories

    androidx.compose.material.ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetState = uiState.sheetState,
        sheetContent = {
            StoryMoreBottomSheet(onStorySheetUIAction = {})
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (stories != null && stories.isNotEmpty())
                StoryCarousel(
                    stories = stories,
                    currentStep = uiState.currentStep,
                    pagerState = pagerState,
                    onStoryOpen = onStoryOpen,
                    onStoryFinish = {
                        onNavigateBack()
                    },
                    onUserClick = onUserClick,
                    value = uiState.input,
                    onInputChange = onInputChange,
                    onSendReaction = onSendReaction,
                    showInterstitialAd = showInterstitialAd,
                    onStoryUIAction = onStoryUIAction,
                    onPauseChange = onPauseChange,
                    onCurrentStepChange = onCurrentStepChange,
                )
        }
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
    currentStep: Int,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
    onPauseChange: (Boolean) -> Unit,
    onCurrentStepChange: (Int) -> Unit,
) {
    HorizontalPager(
        count = 1,
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->

        val isPressed = remember { mutableStateOf(false) }
        val story = stories[currentStep]

        LaunchedEffect(story) {
            if (!story.viewed)
                onStoryOpen(story.id)
        }

        if ((page - 1) % 3 == 0) {
            showInterstitialAd()
        }

        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
        Scaffold(
            bottomBar = {
                StoryFooter(
                    story = story,
                    messageInput = value,
                    isUserMe = story.authorId == FirebaseAuth.getInstance().currentUser?.uid!!,
                    onInputChange = onInputChange,
                    onSendReaction = { onSendReaction(story, it) },
                    onFocusChange = { onPauseChange(it) },
                    onStoryUIAction = onStoryUIAction,
                )
            },
            containerColor = Color.Black,
            modifier = Modifier.carousel(pageOffset)
        ) { padding ->
            PrettyImage(
                data = story.photo,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(padding)
                    .aspectRatio(9 / 16f)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        val maxWidth = this.size.width
                        detectTapGestures(
                            onPress = {
                                val pressStartTime = System.currentTimeMillis()
                                isPressed.value = true
                                tryAwaitRelease()
                                val pressEndTime = System.currentTimeMillis()
                                val totalPressTime = pressEndTime - pressStartTime

                                if (totalPressTime < 200) {
                                    val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                                    if (isTapOnRightThreeQuarters) {
                                        if ((currentStep + 1) >= stories.size)
                                            onStoryFinish()
                                        else
                                            onCurrentStepChange(currentStep + 1)
                                    } else
                                        onCurrentStepChange(currentStep - 1)
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
                onStoryFinish = { last ->
                    if (last)
                        onStoryFinish()
                    else
                        onCurrentStepChange(currentStep + 1)
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
    onStoryFinish: (Boolean) -> Unit,
    onUserClick: (String) -> Unit,
    pause: Boolean,
) {
    Column {
        StoryProgressBar(
            steps = count,
            currentStep = currentStep,
            paused = pause,
            onStepFinish = onStoryFinish,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        PostHeader(
            username = story.username,
            verified = story.verified,
            createTime = story.createTime,
            beverage = Beverage.NONE,
            darkMode = true,
            public = false,
            locationName = "",
            picture = story.profilePictureUrl,
            onHeaderClicked = onUserClick,
            onMoreClicked = {},
        )
    }
}

@Composable
fun StoryFooter(
    story: Story,
    messageInput: String,
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
                value = messageInput,
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
        Item(
            text = "Activity",
            icon = Icons.Outlined.AccountCircle,
            onClick = {
                onStoryUIAction(StoryUIAction.OnActivity, story.id)
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
                    onStoryUIAction(StoryUIAction.OnMore, story.id)
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
            .clip(MaterialTheme.shapes.medium)
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
