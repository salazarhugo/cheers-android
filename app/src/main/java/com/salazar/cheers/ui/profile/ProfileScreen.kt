package com.salazar.cheers.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onSwipeRefresh: () -> Unit,
    onSettingsClicked: () -> Unit,
    onEditProfileClicked: () -> Unit,
    onLikeClicked: (Post) -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onStatClicked: (statName: String, username: String) -> Unit,
    navigateToProfileMoreSheet: () -> Unit,
    onWebsiteClicked: (String) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = onSwipeRefresh,
    ) {
        when (uiState) {
            is ProfileUiState.Loading -> LoadingScreen()
            is ProfileUiState.HasUser -> Profile(
                uiState = uiState,
                onEditProfileClicked = onEditProfileClicked,
                onLikeClicked = onLikeClicked,
                onPostClicked = onPostClicked,
                onStatClicked = onStatClicked,
                navigateToProfileMoreSheet = navigateToProfileMoreSheet,
                onWebsiteClicked = onWebsiteClicked,
            )
        }
    }
}

@Composable
fun Profile(
    uiState: ProfileUiState.HasUser,
    onEditProfileClicked: () -> Unit,
    onLikeClicked: (Post) -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onStatClicked: (statName: String, username: String) -> Unit,
    navigateToProfileMoreSheet: () -> Unit,
    onWebsiteClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(uiState = uiState, navigateToProfileMoreSheet) }
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Section1(user = uiState.user, onStatClicked = onStatClicked)
                Section2(user = uiState.user, onWebsiteClicked = onWebsiteClicked)
                Spacer(Modifier.height(4.dp))
                Row {
                    OutlinedButton(
                        onClick = onEditProfileClicked,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
//                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("Edit Profile", color = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.BookmarkBorder, "")
                    }
                }
            }
            ProfilePostsAndTags(
                uiState = uiState,
                onLikeClicked = onLikeClicked,
                onPostClicked = onPostClicked,
            )
        }
    }
}

@Composable
fun ProfilePostsAndTags(
    uiState: ProfileUiState.HasUser,
    onLikeClicked: (Post) -> Unit,
    onPostClicked: (postId: String) -> Unit,
) {
    val tabs = listOf(Icons.Default.GridView, Icons.Outlined.Email, Icons.Outlined.Celebration)
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, icon ->
            Tab(
                icon = { Icon(icon, null) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
//                        viewModel.toggle()
                },
            )
        }
    }
    HorizontalPager(
        count = tabs.size,
        state = pagerState,
    ) { page ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            val posts = uiState.posts
            val tweets = posts.filter { it.type == PostType.TEXT }
            val postsWithoutTweets =
                posts.filter { it.type == PostType.IMAGE || it.type == PostType.VIDEO }
            when (page) {
                0 -> GridViewPosts(
                    posts = postsWithoutTweets,
                    onPostClicked = onPostClicked,
                )
                1 -> Tweets(
                    tweets = tweets,
                    onLikeClicked = onLikeClicked,
                )
                2 -> FunctionalityNotAvailablePanel()
            }
        }
    }
}

@Composable
fun Tweets(
    tweets: List<Post>,
    onLikeClicked: (Post) -> Unit
) {
    LazyColumn(Modifier.height(800.dp)) {
        items(tweets) { tweet ->
            Tweet(tweet, onLikeClicked = onLikeClicked)
            DividerM3()
        }
    }
}

@Composable
fun Tweet(
    post: Post,
    onLikeClicked: (Post) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = rememberImagePainter(
                data = null,//post.creator.profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentDescription = null,
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
//            Username(
//                username = post.creator.username,
//                verified = post.creator.verified,
//                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
//            )
            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                val size = Modifier.size(22.dp)
                LikeButton(
                    like = post.liked,
                    likes = post.likes,
                    onToggle = { onLikeClicked(post) }
                )
                Icon(
                    painter = rememberImagePainter(R.drawable.ic_bubble_icon),
                    null,
                    modifier = size
                )
                Icon(Icons.Outlined.Repeat, null, modifier = size)
            }
        }
    }
}

@Composable
fun GridViewPosts(
    posts: List<Post>,
    onPostClicked: (postId: String) -> Unit,
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 3),
        modifier = Modifier.height(800.dp)
    ) {
        items(posts) { post ->
            PostItem(post, onPostClicked)
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
) {
    Box(
        modifier = Modifier.padding(1.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        val url = if (post.type == PostType.VIDEO) post.videoThumbnailUrl else post.photoUrl
        PrettyImage(
            data = url,
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
                .clickable {
                    onPostClicked(post.id)
                }
        )

        if (post.type == PostType.VIDEO)
            PlayIcon()
    }
}

@Composable
fun PlayIcon() {
    Icon(
        Icons.Rounded.PlayArrow,
        null,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun Section2(
    user: User,
    onWebsiteClicked: (String) -> Unit,
) {
    Column {
        Row() {
            Text(
                text = user.fullName,
                style = Typography.bodyMedium
            )
            if (user.verified) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "VIP",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            user.bio,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
        )
        ClickableText(
            text = AnnotatedString(user.website),
            style = TextStyle(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Normal
            ),
            onClick = { offset ->
                onWebsiteClicked(user.website)
            },
        )
    }
}

@Composable
fun Toolbar(
    uiState: ProfileUiState.HasUser,
    navigateToProfileMoreSheet: () -> Unit,
) {
    val otherUser = uiState.user

    Column {
        SmallTopAppBar(
            title = {
                Username(
                    username = otherUser.username,
                    verified = otherUser.verified,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    ),
                )
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(id = R.drawable.ic_add_box_white), "")
                }
                IconButton(onClick = navigateToProfileMoreSheet) {
                    Icon(painter = painterResource(id = R.drawable.ic_more_vert_icon), "")
                }
            },
        )
        DividerM3()
    }
}

@Composable
fun Section1(
    user: User,
    onStatClicked: (statName: String, username: String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .fillMaxWidth()
    ) {

        Image(
            painter = rememberImagePainter(
                data = user.profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentDescription = null,
        )
        ProfileStats(user, onStatClicked)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ProfileStats(
    user: User,
    onStatClicked: (statName: String, username: String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        val items = listOf(
            Counter("Posts", user.postCount, null),
            Counter("Followers", user.followers, R.id.followersFollowingFragment),
            Counter("Following", user.following, R.id.followersFollowingFragment),
        )

        items.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    if (item.navId != null) {
                        onStatClicked(item.name, user.username)
                    }
                }
            ) {
                Text(
                    text = item.value.toString(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                )
                Text(text = item.name, fontSize = 14.sp, fontFamily = Roboto)
            }
        }
    }
}
