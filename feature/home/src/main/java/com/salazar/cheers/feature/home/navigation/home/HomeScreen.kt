package com.salazar.cheers.feature.home.navigation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.ads.nativead.NativeAd
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.EmptyFeed
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.ui.AddFriendButton
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.components.circular_progress.CircularProgressComponent
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.feature.home.navigation.NoteList
import com.salazar.cheers.feature.home.navigation.ads.NativeAdView
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                uiState = uiState,
                onSearchClick = { onHomeUIAction(HomeUIAction.OnSearchClick) },
                notificationCount = uiState.notificationCount,
                onActivityClick = { onHomeUIAction(HomeUIAction.OnActivityClick) },
                onChatClick = { onHomeUIAction(HomeUIAction.OnChatClick) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onHomeUIAction(HomeUIAction.OnCreatePostClick) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Floating action button.",
                )
            }
        },
    ) {
        val state = rememberRefreshLayoutState()
        val scope  = rememberCoroutineScope()
        LaunchedEffect(uiState.isLoading) {
            if (!uiState.isLoading) {
                scope.launch {
                    state.finishRefresh(true, delay = 0L)
                }
            }
        }
        PullToRefreshComponent(
            state = state,
            onRefresh = { onHomeUIAction(HomeUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
            ) {
                PostList(
                    uiState = uiState,
                    onHomeUIAction = onHomeUIAction,
                )
            }
        }
    }
}

@Composable
fun PostList(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    LazyColumn(
        state = uiState.listState,
    ) {
        notes(
            uiState = uiState,
            onHomeUIAction = onHomeUIAction,
        )

        item {
            WhatsUpSection(
                avatar = uiState.account?.picture ?: "",
                onClick = { onHomeUIAction(HomeUIAction.OnCreatePostClick) },
            )
            HorizontalDivider()
        }

        item {
            UploadingSection()
        }

        posts(
            posts = uiState.posts,
            audioState = uiState.audioState,
            audioPostID = uiState.audioPostID,
            isLoading = uiState.isLoading,
            endReached = uiState.endReached,
            nativeAd = uiState.nativeAd,
            isSignedIn = uiState.account != null,
            onHomeUIAction = onHomeUIAction,
        )

        item {
            if (uiState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressComponent()
                }
            }
        }

        suggestions(
            suggestions = uiState.suggestions,
            onHomeUIAction = onHomeUIAction,
        )
    }
}

private fun LazyListScope.partiesBanner(onClick: () -> Unit) {
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onClick,
        ) {
            TextButton(onClick = onClick) {
                Text(text = "Parties")
            }
        }
    }
}

private fun LazyListScope.emptyPosts(
    isSignedIn: Boolean,
    onClick: () -> Unit = {},
) {
    item {
        EmptyFeed(
            isSignedIn = isSignedIn,
            modifier = Modifier.padding(vertical = 60.dp),
            onClick = onClick,
        )
    }
}

private fun LazyListScope.suggestions(
    suggestions: List<UserItem>?,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (suggestions == null)
        return

    items(
        items = suggestions,
    ) { user ->
        UserItem(
            userItem = user,
            onClick = {},
            content = {
                AddFriendButton(
                    requestedByViewer = user.requested,
                    onAddFriendClick = {
                        onHomeUIAction(HomeUIAction.OnAddFriendClick(user.id))
                    },
                    onCancelFriendRequestClick = {},
                    onDelete =  {},
                )
            }
        )
    }
}

private fun LazyListScope.notes(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    item {
        NoteList(
            picture = uiState.account?.picture,
            notes = uiState.notes,
            onCreateNoteClick = { onHomeUIAction(HomeUIAction.OnCreateNoteClick) },
            onNoteClick = { onHomeUIAction(HomeUIAction.OnNoteClick(it)) },
        )
        HorizontalDivider()
    }
}

private fun LazyListScope.posts(
    audioPostID: String,
    audioState: AudioState,
    posts: List<Post>,
    nativeAd: NativeAd?,
    isLoading: Boolean,
    isSignedIn: Boolean,
    endReached: Boolean,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (posts.isEmpty() && isLoading.not()) {
        emptyPosts(
            isSignedIn = isSignedIn,
            onClick = {
                onHomeUIAction(HomeUIAction.OnCreatePostClick)
            },
        )
    }

    items(
        count = posts.size,
        key = { posts[it].id },
    ) { i ->
        if (i != 0 && i % 4 == 0 && nativeAd != null) {
            NativeAdView(
                ad = nativeAd,
            )
        }
        val post = posts[i]
        if (i >= posts.size - 1 && !endReached && !isLoading) {
            LaunchedEffect(Unit) {
                onHomeUIAction(HomeUIAction.OnLoadNextItems)
            }
        }

        val isAudioFocused = post.id == audioPostID

        PostComponent(
            post = post,
            audioState = audioState.copy(
                isAudioPlaying = isAudioFocused && audioState.isAudioPlaying,
                audioProgress = if (isAudioFocused) audioState.audioProgress else 0f,
            ),
            modifier = Modifier.animateItemPlacement(),
            onAudioClick = {
                onHomeUIAction(HomeUIAction.OnAudioClick(post.id, post.audioUrl))
            },
            onUserClick = { userID ->
                onHomeUIAction(HomeUIAction.OnUserClick(userID))
            },
            onMoreClick = {
                onHomeUIAction(HomeUIAction.OnPostMoreClick(post.id))
            },
            onLikeClick = {
                onHomeUIAction(HomeUIAction.OnLikeClick(post))
            },
            onLikeCountClick = {
                onHomeUIAction(HomeUIAction.OnPostLikesClick(post.id))
            },
            onCommentClick = {
                onHomeUIAction(HomeUIAction.OnPostCommentClick(post.id))
            },
        )
    }
}

@Composable
fun HomeLazyPagingListState(
    lazyPagingItems: LazyPagingItems<Post>,
) {
    lazyPagingItems.apply {
        when {
            loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount < 1 -> {
//                NoPosts()
            }

            loadState.refresh is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }

            loadState.append is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            loadState.append is LoadState.Error -> {
                val e = lazyPagingItems.loadState.append as LoadState.Error
                Text(
                    text = e.error.localizedMessage!!,
                )
            }
        }
    }
}

@Composable
fun UploadingSection() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("post_upload")
        .observeAsState()
        .value
    val uploadInfo = workInfos?.firstOrNull()

    if (uploadInfo?.state?.isFinished == false)
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (uploadInfo.state) {
                    WorkInfo.State.ENQUEUED ->
                        Text(
                            text = "Will automatically post when possible",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        )

                    WorkInfo.State.RUNNING ->
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(22.dp)),
                        )

                    else -> {}
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { workManager.cancelUniqueWork("post_upload") }) {
                    Icon(Icons.Outlined.Close, contentDescription = null)
                }
            }

            HorizontalDivider()
        }
}

@Composable
fun WhatsUpSection(
    avatar: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarComponent(
            avatar = avatar,
            size = 36.dp,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "What's up party people?",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onClick) {
            Icon(
                Icons.Outlined.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

//@Composable
//fun Stories(
//    uiState: HomeUiState,
//    onStoryClick: (Int) -> Unit,
//    onAddStoryClick: () -> Unit,
//) {
//    val userWithStoriesList = uiState.userWithStoriesList
//    val profilePictureUrl = uiState.user?.picture
////    val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }
//
//    LazyRow(
//        state = rememberLazyListState(),
//        modifier = Modifier.padding(bottom = 8.dp),
//    ) {
//        val user = uiState.user
//        if (user != null)
//            item {
//                YourStory(
//                    profilePictureUrl = profilePictureUrl,
//                    onClick = {
//                        if (user.storyState == com.salazar.cheers.core.model.StoryState.SEEN || user.storyState == com.salazar.cheers.core.model.StoryState.NOT_SEEN)
//                            onStoryClick(0)
//                        else
//                            onAddStoryClick()
//                    },
//                    storyState = user.storyState,
//                )
//            }
//
//        itemsIndexed(
//            items = userWithStoriesList,
//            key = { _, userWithStories: UserWithStories ->
//                userWithStories.user.id
//            },
//        ) { i, userWithStories ->
//            val user = userWithStories.user
//            val stories = userWithStories.stories
//            val viewed = remember(userWithStories.stories) {
//                stories.all { it.viewed }
//            }
//
//            Story(
//                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
//                username = user.username,
//                viewed = viewed,
//                picture = user.picture,
//                onStoryClick = { onStoryClick(i) },
//            )
//        }
//    }
//}

@ScreenPreviews
@Composable
private fun HomeScreenPreview() {
    CheersPreview {
        HomeScreen(
            uiState = HomeUiState(),
            onHomeUIAction = {},
        )
    }
}
