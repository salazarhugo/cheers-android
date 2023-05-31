package com.salazar.cheers.ui.main.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.domain.model.UserWithStories
import com.salazar.cheers.notes.ui.NoteList
import com.salazar.cheers.post.ui.item.PostItem
import com.salazar.cheers.ui.compose.DividerM3
import com.salazar.cheers.ui.compose.ads.NativeAdPost
import com.salazar.cheers.ui.compose.items.UserItem
import com.salazar.cheers.ui.compose.post.NoPosts
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.ui.compose.story.Story
import com.salazar.cheers.ui.compose.story.YourStory
import com.salazar.cheers.user.ui.AddFriendButton


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
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
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
        item {
            NoteList(
                picture = uiState.user?.picture,
                notes = uiState.notes,
                yourNote = uiState.yourNote,
                onCreateNoteClick = { onHomeUIAction(HomeUIAction.OnCreateNoteClick) },
                onNoteClick = { onHomeUIAction(HomeUIAction.OnNoteClick(it)) },
            )
            DividerM3()
        }

        /* item {
            Stories(
                uiState = uiState,
                onStoryClick = { onHomeUIAction(HomeUIAction.OnStoryFeedClick(it)) },
                onAddStoryClick = { onHomeUIAction(HomeUIAction.OnAddStoryClick) },
            )
            DividerM3()
        } */

        item {
            WhatsUpSection(
                avatar = uiState.user?.picture ?: "",
                onClick = { onHomeUIAction(HomeUIAction.OnCreatePostClick) },
            )
            DividerM3()
        }

        item {
            UploadingSection()
        }

        items(
            count = uiState.posts.size,
            key = { uiState.posts[it].id },
        ) { i ->
            NativeAdPost(
                index = i,
                ad = uiState.nativeAd,
            )

            val post = uiState.posts[i]
            if (i >= uiState.posts.size - 1 && !uiState.endReached && !uiState.isLoading) {
                LaunchedEffect(Unit) {
                    onHomeUIAction(HomeUIAction.OnLoadNextItems)
                }
            }
            PostItem(
                post = post,
                onHomeUIAction = onHomeUIAction,
                modifier = Modifier.animateItemPlacement(),
            )
        }

        item {
            if (uiState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (uiState.suggestions != null)
            items(items = uiState.suggestions) { user ->
                UserItem(
                    userItem = user,
                    onClick = {
    //                    onFriendRequestsUIAction(FriendRequestsUIAction.OnUserClick(user.username))
                    },
                    content = {
                        AddFriendButton(
                            requestedByViewer = user.requested,
                            onAddFriendClick = {
                                onHomeUIAction(HomeUIAction.OnAddFriendClick(user.id))
                            },
                            onCancelFriendRequestClick = {
    //                            onFriendRequestsUIAction(FriendRequestsUIAction.OnCancelFriendRequestClick(user.id))
                            },
                            onDelete = {
    //                            onFriendRequestsUIAction(FriendRequestsUIAction.OnRemoveSuggestion(user))
                            }
                        )
                    }
                )
            }
    }
}

@Composable
fun HomeLazyPagingListState(
    lazyPagingItems: LazyPagingItems<Post>,
) {
    lazyPagingItems.apply {
        when {
            loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount < 1 -> {
                NoPosts()
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
        Column() {
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

            DividerM3()
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
        UserProfilePicture(
            picture = avatar,
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

@Composable
fun Stories(
    uiState: HomeUiState,
    onStoryClick: (Int) -> Unit,
    onAddStoryClick: () -> Unit,
) {
    val userWithStoriesList = uiState.userWithStoriesList
    val profilePictureUrl = uiState.user?.picture
    val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }

    LazyRow(
        state = rememberLazyListState(),
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        val user = uiState.user
        if (user != null)
            item {
                YourStory(
                    profilePictureUrl = profilePictureUrl,
                    onClick = {
                        if (user.storyState == com.salazar.cheers.core.model.StoryState.SEEN || user.storyState == com.salazar.cheers.core.model.StoryState.NOT_SEEN)
                            onStoryClick(0)
                        else
                            onAddStoryClick()
                    },
                    storyState = user.storyState,
                )
            }

        itemsIndexed(
            items = userWithStoriesList,
            key = { _, userWithStories: UserWithStories ->
                userWithStories.user.id
            },
        ) { i, userWithStories ->
            val user = userWithStories.user
            val stories = userWithStories.stories
            val viewed = remember(userWithStories.stories) {
                stories.all { it.viewed }
            }

            Story(
                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
                username = user.username,
                viewed = viewed,
                picture = user.picture,
                onStoryClick = { onStoryClick(i) },
            )
        }
    }
}