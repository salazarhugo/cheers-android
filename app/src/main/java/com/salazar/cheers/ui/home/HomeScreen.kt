package com.salazar.cheers.ui.home

import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.*
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.theme.Typography
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.image
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onRefreshPosts: () -> Unit,
    navActions: CheersNavigationActions,
    onPostClicked: (postId: String) -> Unit,
    onPostMoreClicked: (postId: String, isAuthor: Boolean) -> Unit,
    onUserClicked: (username: String) -> Unit,
    navigateToAddEvent: () -> Unit,
    navigateToAddPost: () -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToSearch: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onLike: (post: Post) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = onRefreshPosts,
    ) {
        val showDivider =
            if (uiState is HomeUiState.HasPosts)
                uiState.listState.firstVisibleItemIndex > 0
            else
                false

        var toState by remember { mutableStateOf(MultiFabState.COLLAPSED) }
        Scaffold(
            topBar = {
                Column(
//                    modifier = Modifier.background(Purple200)
                ) {
                    MyAppBar(
                        navigateToSearch = navigateToSearch,
                        onSelectTab = onSelectTab,
                        tab = uiState.selectedTab,
                    )
                    if (showDivider)
                        DividerM3()
                }
            },
            floatingActionButton = {
                MultiFloatingActionButton(
                    Icons.Default.Add,
                    listOf(
                        MultiFabItem(
                            "event",
                            Icons.Outlined.Event,
                            "Party",
                        ),
                        MultiFabItem(
                            "post",
                            Icons.Outlined.PostAdd,
                            "Hangout",
                        )
                    ), toState, true, { state ->
                        toState = state
                    },
                    onFabItemClicked = {
                        toState = MultiFabState.COLLAPSED
                        if (it.identifier == "event")
                            navigateToAddEvent()
                        else
                            navigateToAddPost()
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
            ) {
                if (uiState.isLoading)
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                when (uiState) {
                    is HomeUiState.HasPosts -> {
                        if (uiState.postsFlow.collectAsLazyPagingItems().itemSnapshotList.size == 0)
                            NoPosts(uiState = uiState)
                        else
                            PostList(
                                uiState = uiState,
                                navActions = navActions,
                                onPostClicked = onPostClicked,
                                onUserClicked = onUserClicked,
                                onPostMoreClicked = onPostMoreClicked,
                                onLike = onLike,
                                navigateToComments = navigateToComments
                            )
                    }
                }
            }
            val alpha = if (toState == MultiFabState.EXPANDED) 0.9f else 0f
            Box(
                modifier = Modifier
                    .alpha(animateFloatAsState(alpha).value)
                    .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                    .fillMaxSize()
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
                    rememberImagePainter(data = ad.icon.uri),
                    null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (ad.headline != null)
                Text(ad.headline, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Text(
                    text = "Sponsored",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        DividerM3()
        val context = LocalContext.current;
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
                        this.image = it.drawable
                    })
                }

                if (ad.mediaContent.hasVideoContent()) {
//                    val context = ()
//                    val mediaView = MediaView(context).apply {
//                        setMediaContent(ad.mediaContent!!)
//                        setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                    }
//                    adView.addView(mediaView)
//                    adView.mediaView = mediaView
                }
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
        tabs.forEachWithIndex { i, s ->
            val a = if (tab == tabs.indexOf(s)) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            FilledTonalButton(
                onClick = { onSelectTab(i) },
                colors = a,
            ) { Text(s) }
            if (i != tabs.size-1)
                Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun NoPosts(uiState: HomeUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(22.dp),
    ) {
        val suggestions = uiState.suggestions
//            if (suggestions != null)
//                Suggestions(suggestions = suggestions)
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
//            Spacer(Modifier.height(24.dp))
//            ConnectContacts()
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
                    painter = rememberImagePainter(
                        data = suggestedUser.user.profilePictureUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        },
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
                    text = suggestedUser.user.fullName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(
                    modifier = Modifier.padding(0.dp, 12.dp)
                ) {
                    repeat(3) {
                        Image(
                            painter = rememberImagePainter(suggestedUser.posts.getOrNull(it)?.photoUrl),
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
    navActions: CheersNavigationActions,
    onPostClicked: (postId: String) -> Unit,
    onUserClicked: (username: String) -> Unit,
    onPostMoreClicked: (postId: String, Boolean) -> Unit,
    onLike: (post: Post) -> Unit,
    navigateToComments: (String) -> Unit,
) {
    val posts = uiState.postsFlow.collectAsLazyPagingItems()
    val events = uiState.eventsFlow.collectAsLazyPagingItems()

    LazyColumn(state = uiState.listState) {
        if (uiState.selectedTab == 1)
            items(events) { event ->
                Event(event!!)
            }
        else {
            itemsIndexed(posts) { i, post ->
                if (i != 0 && i % 4 == 0 && uiState.nativeAd != null) {
                    DividerM3()
                    NativeAdPost(ad = uiState.nativeAd)
                }
                when (post?.post?.type) {
                    PostType.TEXT -> Post(
                        modifier = Modifier.animateItemPlacement(),
                        postFeed = post,
                        navigateToComments = navigateToComments,
                        likes = uiState.likes,
                        isPostVisible = true,
                        navActions = navActions,
                        onPostClicked = onPostClicked,
                        onUserClicked = onUserClicked,
                        onPostMoreClicked = onPostMoreClicked,
                        onLike = onLike,
                    )
                    PostType.IMAGE -> Post(
                        modifier = Modifier.animateItemPlacement(),
                        postFeed = post,
                        navigateToComments = navigateToComments,
                        likes = uiState.likes,
                        isPostVisible = true,
                        navActions = navActions,
                        onPostClicked = onPostClicked,
                        onUserClicked = onUserClicked,
                        onPostMoreClicked = onPostMoreClicked,
                        onLike = onLike,
                    )
                    PostType.VIDEO -> Post(
                        modifier = Modifier.animateItemPlacement(),
                        postFeed = post,
                        navigateToComments = navigateToComments,
                        likes = uiState.likes,
                        isPostVisible = true,
                        navActions = navActions,
                        onPostClicked = onPostClicked,
                        onUserClicked = onUserClicked,
                        onPostMoreClicked = onPostMoreClicked,
                        onLike = onLike,
                    )
                }

            }
        }

        posts.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicatorM3(
                            modifier = Modifier
                                .fillMaxWidth()
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
    postFeed: PostFeed,
    likes: Set<String>,
    modifier: Modifier = Modifier,
    isPostVisible: Boolean,
    navActions: CheersNavigationActions,
    onPostClicked: (postId: String) -> Unit,
    onPostMoreClicked: (postId: String, Boolean) -> Unit,
    onUserClicked: (username: String) -> Unit,
    onLike: (post: Post) -> Unit,
    navigateToComments: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        PostHeader(postFeed, onUserClicked, onPostMoreClicked)
//            if (post.type != PostType.TEXT)
//                DividerM3()
        PostBody(postFeed.post, isPostVisible, onPostClicked = onPostClicked, onLike = onLike)
        PostFooter(postFeed, navActions, onLike = onLike, navigateToComments = navigateToComments)
    }
}

@Composable
fun PostHeader(
    postFeed: PostFeed,
    onUserClicked: (username: String) -> Unit,
    onPostMoreClicked: (postId: String, Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val post = postFeed.post
    val author = postFeed.author

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .clickable { onUserClicked(author.username) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            val brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD41668),
                    Color(0xFFF9B85D),
                )
            )

            Image(
                painter = rememberImagePainter(
                    data = author.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .border(1.2.dp, brush, CircleShape)
                    .size(36.dp)
                    .padding(3.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Username(
                    username = author.username,
                    verified = author.verified,
                    textStyle = Typography.bodyMedium
                )
                if (post.locationName.isBlank())
                    Text(
                        post.relativeTime,
                        style = Typography.labelMedium
                    )
                else
                    Text(text = post.locationName, style = Typography.labelSmall)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
//                Box(
//                    modifier = Modifier
//                        .padding(end = 4.dp)
//                        .size(4.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.onBackground)
//                )
            if (post.locationName.isNotBlank())
                Text(
                    post.relativeTime,
                    style = Typography.labelMedium
                )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onPostMoreClicked(
                        post.id,
                        author.id == FirebaseAuth.getInstance().currentUser?.uid
                    )
                }
            ) {
                Icon(
                    Icons.Default.MoreHoriz, null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun PostBody(
    post: Post,
    isPostVisible: Boolean,
    onPostClicked: (postId: String) -> Unit,
    onLike: (post: Post) -> Unit,
) {
    Box {
        if (post.videoUrl.isNotBlank())
            VideoPlayer(
                uri = post.videoUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 5f)
            )
        else if (post.photoUrl.isNotBlank())
            PrettyImage(
                data = post.photoUrl,
                contentDescription = "avatar",
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)// or 4/5f
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onLike(post) },
                        )
                    }
                    .clickable { onPostClicked(post.id) }
            )
        else
            SelectionContainer() {
                Text(
                    text = post.caption,
                    modifier = Modifier.padding(16.dp)
                )
            }

//        if (post.tagUsers.isNotEmpty())
//            InThisPhotoAnnotation(modifier = Modifier.align(Alignment.BottomStart))
    }
}

@Composable
fun InThisPhotoAnnotation(modifier: Modifier) {
    AnimateVisibilityFade(modifier = modifier) {
        Surface(
            modifier = Modifier
                .padding(32.dp)
                .clickable {},
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                modifier = Modifier
                    .padding(6.dp)
                    .size(15.dp),
                contentDescription = null
            )
        }
    }
}

@Composable
fun PostFooterButtons(
    post: Post,
    onLike: (post: Post) -> Unit,
    navigateToComments: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                like = post.liked,
                likes = post.likes,
                onToggle = { onLike(post) },
            )
            Icon(
                modifier = Modifier.clickable { navigateToComments(post.id) },
                painter = rememberImagePainter(R.drawable.ic_bubble_icon),
                contentDescription = null
            )
            Icon(Icons.Outlined.Share, null)
        }
        Icon(Icons.Outlined.BookmarkBorder, null)
    }
}

@Composable
fun PostFooter(
    postFeed: PostFeed,
    navActions: CheersNavigationActions,
    onLike: (post: Post) -> Unit,
    navigateToComments: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        PostFooterButtons(postFeed.post, onLike = onLike, navigateToComments = navigateToComments)
        if (postFeed.post.type != PostType.TEXT) {
            LikedBy(post = postFeed.post, navActions)
//            if (post.tagUsers.isNotEmpty())
//                TagUsers(post.tagUsers)
            if (postFeed.post.caption.isNotBlank())
                Caption(
                    username = postFeed.author.username,
                    caption = postFeed.post.caption,
                )
        }
    }
    if (postFeed.post.type != PostType.TEXT)
        Spacer(Modifier.height(12.dp))
}

@Composable
fun LikedBy(
    post: Post,
    navActions: CheersNavigationActions
) {
//    Text(
//        "${post.likes} ${if (post.likes > 1) "likes" else "like"}",
//        style = MaterialTheme.typography.bodyMedium,
//        fontWeight = FontWeight.Bold,
//        modifier = Modifier.clickable {
//            navActions.navigateToLikes(post.id)
//        }
//    )
}

@Composable
fun Caption(
    username: String,
    caption: String,
) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(username)
            }
            append(" ")
            append(caption)
        },
        style = Typography.bodyMedium
    )
}

@Composable
fun TagUsers(tagUsers: List<User>) {
    Row {
        val n = Math.min(tagUsers.size, 3)
        repeat(n) { i ->
            val u = tagUsers[i]
            Image(
                painter = rememberImagePainter(
                    data = u.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        crossfade(true)
                        placeholder(R.drawable.default_profile_picture)
                    }
                ),
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = -(8 * i).dp)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentDescription = null,
            )
        }
        Text(
            buildAnnotatedString {
                append("With ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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
    onSelectTab: (Int) -> Unit,
    navigateToSearch: () -> Unit,
) {
    val icon =
        if (isSystemInDarkTheme()) R.drawable.ic_cheers_logo else R.drawable.ic_cheers_logo
    CenterAlignedTopAppBar(
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior { true },
        colors = TopAppBarDefaults.smallTopAppBarColors(
//            containerColor = Purple200
        ),
//        CenterAlignedTopAppBar(
//            modifier = Modifier.height(50.dp),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier
                        .size(34.dp),
                    contentDescription = "",
                )
                TopTabs(tab = tab, onSelectTab = onSelectTab)
                IconButton(onClick = navigateToSearch) {
                    Icon(
                        painter = rememberImagePainter(data = R.drawable.ic_search_icon),
                        contentDescription = "Search icon"
                    )
                }
            }
        },
        actions = {
//            IconButton(onClick = { navActions.navigateToSearch() }) {
//                Icon(
//                    painter = rememberImagePainter(data = R.drawable.ic_search_icon),
//                    contentDescription = "Search icon"
//                )
//            }
//            IconButton(onClick = { navActions.navigateToActivity() }) {
//                Icon(
//                    imageVector = Icons.Outlined.Notifications,
//                    contentDescription = "Activity icon"
//                )
//            }
//            IconButton(onClick = {
//                navActions.navigateToCamera()
//            }) {
//                Icon(
//                    Icons.Outlined.Camera,
//                    contentDescription = "Activity icon"
//                )
//            }
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
                PlayerView(context).apply {
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

@Composable
fun Event(post: EventUi) {
    val event = post.event
    Column(
        modifier = Modifier.clickable { }
    ) {
        Image(
            painter = rememberImagePainter(
                data = event.imageUrl,
                builder = {
                    error(R.drawable.image_placeholder)
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = post.host.profilePictureUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        },
                    ),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(16.dp))
                Username(
                    username = post.host.username,
                    verified = post.host.verified,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
            }

            val d = remember { ZonedDateTime.parse(event.startDate) }
            Text(
                d.toLocalDateTime().format(DateTimeFormatter.ofPattern("E, d MMM hh:mm a")),
                style = MaterialTheme.typography.bodyMedium
            )
            if (event.name.isNotBlank())
                Text(event.name, style = MaterialTheme.typography.titleLarge)
            if (event.description.isNotBlank())
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            if (event.locationName.isNotBlank())
                Text(text = event.locationName, style = Typography.labelSmall)
            Text("4.8k interested - 567 going", modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Star, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Interested")
                }
                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Going")
                }
            }
        }
    }
}

