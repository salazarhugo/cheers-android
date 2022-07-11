package com.salazar.cheers.ui.main.home

import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.*
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.components.post.PostBody
import com.salazar.cheers.components.post.PostFooter
import com.salazar.cheers.components.post.PostHeader
import com.salazar.cheers.components.post.PostText
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.components.story.Story
import com.salazar.cheers.components.story.YourStory
import com.salazar.cheers.components.utils.PrettyImage
import com.salazar.cheers.internal.*
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.main.chats.MyAppBar
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlin.math.absoluteValue


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSwipeRefresh: () -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onPostMoreClicked: (postId: String, authorId: String) -> Unit,
    onUserClicked: (username: String) -> Unit,
    navigateToComments: (Post) -> Unit,
    navigateToSearch: () -> Unit,
    onStoryClick: (String) -> Unit,
    onActivityClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onLike: (post: Post) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    val showDivider by remember {
        derivedStateOf {
            uiState is HomeUiState.HasPosts && uiState.listState.firstVisibleItemIndex > 0
        }
    }

    val toState by remember { mutableStateOf(MultiFabState.COLLAPSED) }
    Scaffold(
        topBar = {
            Column {
                MyAppBar(
                    navigateToSearch = navigateToSearch,
                    tab = uiState.selectedTab,
                    notificationCount = uiState.notificationCount,
                    onActivityClick = onActivityClick,
                )
                if (showDivider)
                    DividerM3()
            }
        },
//        floatingActionButton = {
//            MultiFloatingActionButton(
//                Icons.Default.Add,
//                listOf(
//                    MultiFabItem(
//                        "event",
//                        Icons.Outlined.Event,
//                        "Party",
//                    ),
//                    MultiFabItem(
//                        "post",
//                        Icons.Outlined.PostAdd,
//                        "Hangout",
//                    )
//                ), toState, true, { state ->
//                    toState = state
//                },
//                onFabItemClicked = {
//                    toState = MultiFabState.COLLAPSED
//                    if (it.identifier == "event")
//                        navigateToAddEvent()
//                    else
//                        navigateToAddPost()
//                }
//            )
//        },
        content = {
            SwipeToRefresh(
                state = rememberSwipeToRefreshState(isRefreshing = false),
                onRefresh = { onSwipeRefresh() },
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
                                onPostClicked = onPostClicked,
                                onUserClicked = onUserClicked,
                                onPostMoreClicked = onPostMoreClicked,
                                onLike = onLike,
                                navigateToComments = navigateToComments,
                                onStoryClick = onStoryClick,
                                onAddStoryClick = onAddStoryClick,
                                onCommentClick = onCommentClick,
                            )
                        }
                        else -> {}
                    }
                }
                val alpha = if (toState == MultiFabState.EXPANDED) 0.92f else 0f
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
    val profilePictureUrl = uiState.user?.profilePictureUrl
    val uid by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!)}

    LazyRow(
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        item {
            val user = uiState.user
            if (user != null)
            YourStory(
                profilePictureUrl = profilePictureUrl,
                onClick = {
                    if (user.hasStory)
                        onStoryClick(user.username)
                    else
                        onAddStoryClick()
                          },
                hasStory = user.hasStory,
                seenStory = user.seenStory,
            )
        }
        items(items = stories, key = { it.id }) { story ->
            if (story != null && story.authorId != uid)
                Story(
                    modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
                    username = story.username,
                    seen = story.seen,
                    profilePictureUrl = story.profilePictureUrl,
                    onStoryClick = onStoryClick,
                )
        }
    }
}

@Composable
fun NativeAdPost(ad: NativeAd) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp, 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ad.icon != null) {
                Image(
                    rememberAsyncImagePainter(model = ad.icon!!.uri),
                    null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            val headline = ad.headline
            if (headline != null)
                Text(text = headline, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Text(
                    text = "Ad",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        DividerM3()
        val context = LocalContext.current
        AndroidView(
            factory = {
                val adView = NativeAdView(it)
                adView.setNativeAd(ad)
                adView.addView(TextView(it).apply {
                    text = ad.headline
                })

                ad.images.forEach {
                    adView.addView(ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageDrawable(it.drawable)
                    })
                }

//                if (ad.mediaContent.hasVideoContent()) {
//                    val context = ()
//                    val mediaView = MediaView(context).apply {
//                        setMediaContent(ad.mediaContent!!)
//                        setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                    }
//                    adView.addView(mediaView)
//                    adView.mediaView = mediaView
//                }
                adView
            },
            modifier = Modifier.clickable {
            }
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun TopTabs(
    tab: Int,
    onSelectTab: (Int) -> Unit
) {
    val tabs = listOf("Hangouts", "Parties")
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { i, s ->
            val a =
                if (tab == tabs.indexOf(s)) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            FilledTonalButton(
                onClick = { onSelectTab(i) },
                colors = a,
            ) { Text(s) }
            if (i != tabs.size - 1)
                Spacer(Modifier.width(8.dp))
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
fun Suggestions(suggestions: List<SuggestionUser>) {
    val pagerState = rememberPagerState()

    HorizontalPager(
        count = suggestions.size,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 76.dp),
    ) { page ->
        Suggestion(suggestions[page], this, page)
    }

}

@Composable
fun Suggestion(
    suggestedUser: SuggestionUser,
    scope: PagerScope,
    page: Int
) {
    scope.apply {
        Surface(
            shape = RoundedCornerShape(24.dp),
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                }
            ) {

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = suggestedUser.user.profilePictureUrl).apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            }).build()
                    ),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = suggestedUser.user.username,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = suggestedUser.user.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(
                    modifier = Modifier.padding(0.dp, 12.dp)
                ) {
                    repeat(3) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                suggestedUser.posts.getOrNull(it)?.photos?.get(
                                    0
                                )
                            ),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.0f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Text(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.padding(16.dp)

                ) {
                    Text("Follow")
                }
            }
        }
    }
}

@Composable
fun ConnectContacts() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.Outlined.ContactPage,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                .padding(12.dp)
        )
        Column {
            Text("Connect Contacts", style = MaterialTheme.typography.bodyMedium)
            Text("Follow people you know", style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Connect")
        }
    }
}

@Composable
fun PostList(
    uiState: HomeUiState.HasPosts,
    onPostClicked: (postId: String) -> Unit,
    onUserClicked: (username: String) -> Unit,
    onPostMoreClicked: (postId: String, authorId: String) -> Unit,
    onLike: (post: Post) -> Unit,
    navigateToComments: (Post) -> Unit,
    onStoryClick: (String) -> Unit,
    onAddStoryClick: () -> Unit,
    onCommentClick: (String) -> Unit,
) {
    val posts = uiState.postsFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
    ) {
        item {
            Stories(
                uiState = uiState,
                onStoryClick = onStoryClick,
                onAddStoryClick = onAddStoryClick,
            )
            DividerM3()
        }
        itemsIndexed(posts) { i, post ->
            if ((i - 1) % 3 == 0 && uiState.nativeAd != null) {
                DividerM3()
                NativeAdPost(ad = uiState.nativeAd)
            }
            if (post != null)
                Post(
                    modifier = Modifier.animateItemPlacement(),
                    post = post,
                    navigateToComments = navigateToComments,
                    onPostClicked = onPostClicked,
                    onUserClicked = onUserClicked,
                    onPostMoreClicked = onPostMoreClicked,
                    onLike = onLike,
                    onCommentClick = onCommentClick,
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
fun Post(
    post: Post,
    modifier: Modifier = Modifier,
    onPostClicked: (postId: String) -> Unit,
    onPostMoreClicked: (postId: String, authorId: String) -> Unit,
    onUserClicked: (username: String) -> Unit,
    onLike: (post: Post) -> Unit,
    navigateToComments: (Post) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        PostHeader(
            username = post.username,
            verified = post.verified,
            beverage = Beverage.fromName(post.beverage),
            public = post.privacy == Privacy.PUBLIC.name,
            created = post.created,
            profilePictureUrl = post.profilePictureUrl,
            locationName = post.locationName,
            onHeaderClicked = onUserClicked,
            onMoreClicked = {
                onPostMoreClicked(post.id, post.authorId)
            },
        )
        PostText(
            caption = post.caption,
            onUserClicked = onUserClicked,
            onPostClicked = { onPostClicked(post.id)},
        )
        PostBody(
            post,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)),
            onPostClicked = onPostClicked,
            onLike = onLike,
            pagerState = pagerState
        )
        PostFooter(
            post,
            onLike = onLike,
            navigateToComments = navigateToComments,
            pagerState = pagerState,
            onCommentClick = onCommentClick,
        )
    }
}

@Composable
fun PhotoCarousel(
    modifier: Modifier = Modifier,
    photos: List<String>,
    pagerState: PagerState,
    onPostClick: () -> Unit,
) {
    HorizontalPager(
        count = photos.size,
        state = pagerState,
    ) { page ->
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale *= zoomChange
            offset += offsetChange
        }

        PrettyImage(
            data = photos[page],
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state)
                .clickable { onPostClick() }
        )
    }
}

@Composable
fun TagUsers(tagUsers: List<User>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val n = Math.min(tagUsers.size, 3)
        repeat(n) { i ->
            val u = tagUsers[i]
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = u.profilePictureUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = -(8 * i).dp)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                    .clip(CircleShape),
                contentDescription = null,
            )
        }
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    if (tagUsers.size > 2)
                        append(tagUsers[0].username + ", " + tagUsers[1].username + " and others")
                    else
                        append(tagUsers.joinToString(", ") { it.username })
                }
            },
            style = Typography.bodyMedium
        )
    }
}

@Composable
fun MyAppBar(
    tab: Int,
    notificationCount: Int,
    navigateToSearch: () -> Unit,
    onActivityClick: () -> Unit,
) {
    val icon =
        if (isSystemInDarkTheme()) R.drawable.ic_cheers_logo else R.drawable.ic_cheers_logo
    CenterAlignedTopAppBar(
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState()),
        colors = TopAppBarDefaults.smallTopAppBarColors(
//            containerColor = Purple200
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
                    Badge { Text(text = notificationCount.toString())}
                }) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Search icon"
                    )
                }
            }
            IconButton(onClick = navigateToSearch) {
                Icon(
                    painter = rememberAsyncImagePainter(model = R.drawable.ic_search_icon),
                    contentDescription = "Search icon"
                )
            }
        },
    )
}

@Composable
fun VideoPlayer(
    uri: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Create media item
    val mediaItem = MediaItem.fromUri(uri)

    // Create the player
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(mediaItem)
            this.prepare()
            this.playWhenReady = true
            this.repeatMode = Player.REPEAT_MODE_ALL
            this.volume = 0f
            this.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                StyledPlayerView(context).apply {
                    this.player = player
                }
            },
            modifier = modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    if (player.volume == 0f) player.volume = 1f else player.volume = 0f
                }
        ) {
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
//                it.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        }
    ) {
        onDispose {
            player.release()
        }
    }
}

