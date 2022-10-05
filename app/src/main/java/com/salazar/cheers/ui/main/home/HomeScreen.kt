package com.salazar.cheers.ui.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
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
import com.salazar.cheers.compose.post.*
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.compose.story.Story
import com.salazar.cheers.compose.story.YourStory
import com.salazar.cheers.internal.*
import com.salazar.cheers.ui.main.chats.MyAppBar
import com.salazar.cheers.ui.theme.Roboto


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    navigateToComments: (Post) -> Unit,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    val fabState by remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(
        topBar = {
            HomeTopBar(
                uiState = uiState,
                onSearchClick = { onHomeUIAction(HomeUIAction.OnSearchClick) },
                notificationCount = uiState.notificationCount,
                onActivityClick = {  onHomeUIAction(HomeUIAction.OnActivityClick) },
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
                    when (uiState) {
                        is HomeUiState.HasPosts -> {
                            PostList(
                                uiState = uiState,
                                navigateToComments = navigateToComments,
                                onHomeUIAction = onHomeUIAction,
                            )
                        }
                        else -> {}
                    }
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
fun Stories(
    uiState: HomeUiState.HasPosts,
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

@Composable
fun NoPosts() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(22.dp),
    ) {
        Text(
            "Welcome to Cheers",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Follow people to start seeing the photos and videos they share.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun PostList(
    uiState: HomeUiState.HasPosts,
    navigateToComments: (Post) -> Unit,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    val posts = uiState.postsFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val workInfos = workManager.getWorkInfosForUniqueWorkLiveData("post_upload")
        .observeAsState()
        .value
    val uploadInfo = workInfos?.firstOrNull()

    LazyColumn(
        state = uiState.listState,
        modifier = Modifier.fillMaxHeight(),
    ) {
        item {
            Stories(
                uiState = uiState,
                onStoryClick = { onHomeUIAction(HomeUIAction.OnStoryClick(it))},
                onAddStoryClick = { onHomeUIAction(HomeUIAction.OnAddStoryClick)},
            )
            DividerM3()
        }

        item {
            WhatsUpSection(
                avatar = uiState.user?.picture ?: "",
                onClick = { onHomeUIAction(HomeUIAction.OnAddPostClick)},
            )
            DividerM3()
        }

        if (uploadInfo != null && !uploadInfo.state.isFinished)
            item {
                UploadingSection(
                    uploadInfo = uploadInfo,
                    onCancelWork = { workManager.cancelUniqueWork("post_upload") },
                )
                DividerM3()
            }

        itemsIndexed(posts) { i, post ->
            if ((i - 1) % 3 == 0 && uiState.nativeAd != null) {
                DividerM3()
                NativeAdPost(ad = uiState.nativeAd)
            }
            if (post != null)
                PostView(
                    modifier = Modifier.animateItemPlacement(),
                    post = post,
                    navigateToComments = navigateToComments,
                    onPostClicked = { onHomeUIAction(HomeUIAction.OnPostClick(it))},
                    onUserClicked = { onHomeUIAction(HomeUIAction.OnUserClick(it))},
                    onPostMoreClicked = { a, b -> onHomeUIAction(HomeUIAction.OnPostMoreClick(a, b))},
                    onLike = { onHomeUIAction(HomeUIAction.OnLikeClick(it))},
                    onCommentClick = { onHomeUIAction(HomeUIAction.OnCommentClick(it))},
                )
        }

        posts.apply {
            when {
                loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount < 1 -> {
                    item { NoPosts() }
                }
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicatorM3(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicatorM3(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    val e = posts.loadState.append as LoadState.Error
                    item {
                        Text(
                            text = e.error.localizedMessage!!,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UploadingSection(
    uploadInfo: WorkInfo,
    onCancelWork: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (uploadInfo.state == WorkInfo.State.ENQUEUED)
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = "Will automatically post when possible",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
        if (uploadInfo.state == WorkInfo.State.RUNNING)
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        IconButton(onClick = onCancelWork) {
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
            uiState is HomeUiState.HasPosts && uiState.listState.firstVisibleItemIndex > 0
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