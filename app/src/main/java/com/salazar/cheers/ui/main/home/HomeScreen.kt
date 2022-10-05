package com.salazar.cheers.ui.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import androidx.work.WorkInfo
import androidx.work.WorkManager
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.compose.CircularProgressIndicatorM3
import com.salazar.cheers.compose.DividerM3
import com.salazar.cheers.compose.MultiFabState
import com.salazar.cheers.compose.ads.NativeAdPost
import com.salazar.cheers.compose.post.NoPosts
import com.salazar.cheers.compose.post.PostPlaceholder
import com.salazar.cheers.compose.post.PostView
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.compose.story.Story
import com.salazar.cheers.compose.story.YourStory
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.StoryState
import com.salazar.cheers.ui.theme.Roboto


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    val fabState by remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(
        topBar = {
            HomeTopBar(
                uiState = uiState,
                onSearchClick = { onHomeUIAction(HomeUIAction.OnSearchClick) },
                notificationCount = uiState.notificationCount,
                onActivityClick = { onHomeUIAction(HomeUIAction.OnActivityClick) },
            )
        },
        content = {
            SwipeToRefresh(
                state = rememberSwipeToRefreshState(isRefreshing = false),
                onRefresh = { onHomeUIAction(HomeUIAction.OnSwipeRefresh) },
                modifier = Modifier.padding(it),
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
                val alpha = if (fabState == MultiFabState.EXPANDED) 0.92f else 0f
                Box(
                    modifier = Modifier
                        .alpha(animateFloatAsState(alpha).value)
                        .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                        .fillMaxSize()
                )
            }
        }
    )
}

@Composable
fun PostList(
    uiState: HomeUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    val lazyPagingItems = uiState.postsFlow.collectAsLazyPagingItems()

    LazyColumn(
        state = uiState.listState,
        modifier = Modifier.fillMaxHeight(),
    ) {
        item {
            Stories(
                uiState = uiState,
                onStoryClick = { onHomeUIAction(HomeUIAction.OnStoryClick(it)) },
                onAddStoryClick = { onHomeUIAction(HomeUIAction.OnAddStoryClick) },
            )
            DividerM3()
        }

        item {
            WhatsUpSection(
                avatar = uiState.user?.picture ?: "",
                onClick = { onHomeUIAction(HomeUIAction.OnAddPostClick) },
            )
            DividerM3()
        }

        item {
            UploadingSection()
            DividerM3()
        }

        itemsIndexed(
            items = lazyPagingItems,
            key = { _, post -> post.id },
        ) { i, post ->

            if ((i - 1) % 3 == 0 && uiState.nativeAd != null) {
                DividerM3()
                NativeAdPost(ad = uiState.nativeAd)
            }
            if (post != null)
                PostView(
                    post = post,
                    onHomeUIAction = onHomeUIAction,
                    modifier = Modifier.animateItemPlacement(),
                )
            else
                PostPlaceholder()
        }

        item {
            HomeLazyPagingListState(
                lazyPagingItems = lazyPagingItems,
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
                CircularProgressIndicatorM3(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
            loadState.append is LoadState.Loading -> {
                CircularProgressIndicatorM3(
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (uploadInfo?.state == WorkInfo.State.ENQUEUED)
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = "Will automatically post when possible",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
        if (uploadInfo?.state == WorkInfo.State.RUNNING)
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        IconButton(onClick = { workManager.cancelUniqueWork("post_upload") }) {
            Icon(Icons.Outlined.Close, contentDescription = null)
        }
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserProfilePicture(
            avatar = avatar,
            size = 36.dp,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "What's up party people?",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun HomeTopBar(
    uiState: HomeUiState,
    notificationCount: Int,
    onSearchClick: () -> Unit,
    onActivityClick: () -> Unit,
) {
    val showDivider by remember {
        derivedStateOf {
            uiState.listState.firstVisibleItemIndex > 0
        }
    }

    Column {
        if (showDivider)
            DividerM3()

        val icon =
            if (isSystemInDarkTheme()) R.drawable.ic_cheers_logo else R.drawable.ic_cheers_logo
        CenterAlignedTopAppBar(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            colors = TopAppBarDefaults.smallTopAppBarColors(
            ),
            navigationIcon = {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(34.dp),
                    contentDescription = null,
                )
            },
            title = {
                Text("Friends", fontWeight = FontWeight.Bold, fontFamily = Roboto)
            },
            actions = {
                IconButton(onClick = onActivityClick) {
                    BadgedBox(badge = {
                        if (notificationCount > 0)
                            Badge { Text(text = notificationCount.toString()) }
                    }) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Search icon"
                        )
                    }
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        painter = rememberAsyncImagePainter(model = R.drawable.ic_search_icon),
                        contentDescription = "Search icon"
                    )
                }
            },
        )
    }
}

@Composable
fun Stories(
    uiState: HomeUiState,
    onStoryClick: (String) -> Unit,
    onAddStoryClick: () -> Unit,
) {
    val stories = uiState.storiesFlow?.collectAsLazyPagingItems() ?: return
    val profilePictureUrl = uiState.user?.picture
    val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }

    LazyRow(
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        item {
            val user = uiState.user
            if (user != null)
                YourStory(
                    profilePictureUrl = profilePictureUrl,
                    onClick = {
                        if (user.storyState == StoryState.SEEN || user.storyState == StoryState.NOT_SEEN)
                            onStoryClick(user.username)
                        else
                            onAddStoryClick()
                    },
                    storyState = user.storyState,
                )
        }
        items(items = stories, key = { it.id }) { story ->
            if (story != null && story.authorId != uid)
                Story(
                    modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
                    username = story.username,
                    seenStory = story.seen,
                    profilePictureUrl = story.profilePictureUrl,
                    onStoryClick = onStoryClick,
                )
        }
    }
}