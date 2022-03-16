package com.salazar.cheers.ui.main.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.components.profile.ProfileHeader
import com.salazar.cheers.components.profile.ProfileText
import com.salazar.cheers.internal.*
import com.salazar.cheers.ui.theme.Roboto
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
                ProfileHeader(user = uiState.user, onStatClicked = onStatClicked)
                ProfileText(user = uiState.user, onWebsiteClicked = onWebsiteClicked)
                Spacer(Modifier.height(8.dp))
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
                posts = uiState.posts,
                onPostClicked = onPostClicked,
            )
        }
    }
}

@Composable
fun ProfilePostsAndTags(
    posts: List<Post>,
    onPostClicked: (postId: String) -> Unit,
) {
    val tabs = listOf(Icons.Default.GridView, Icons.Outlined.ViewList, Icons.Outlined.Celebration)
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
            when (page) {
                0 -> GridViewPosts(
                    posts = posts,
                    onPostClicked = onPostClicked,
                )
                1 -> ListViewPosts(
                    posts = posts,
                    onPostClicked = onPostClicked,
                )
                2 -> FunctionalityNotAvailablePanel()
            }
        }
    }
}


@Composable
fun ListViewPosts(
    posts: List<Post>,
    onPostClicked: (postId: String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        // We check if there is an *URL* annotation attached to the text
        // at the clicked position
        modifier = Modifier.height(800.dp),
    ) {
        items(posts) { post ->
            PostItem(post, onPostClicked)
        }
    }
}

@Composable
fun GridViewPosts(
    posts: List<Post>,
    onPostClicked: (postId: String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = Modifier.height(800.dp),
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
    if (post.photos.isEmpty()) return
    Box(
        modifier = Modifier.padding(1.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        val url = if (post.type == PostType.VIDEO) post.videoThumbnailUrl else post.photos[0]
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
                    text = numberFormatter(value = item.value),
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                )
                Text(text = item.name, fontSize = 14.sp, fontFamily = Roboto)
            }
        }
    }
}

//@Composable
//fun Tweets(
//    tweets: List<Post>,
//    onLikeClicked: (Post) -> Unit
//) {
//    LazyColumn(Modifier.height(800.dp)) {
//        items(tweets) { tweet ->
//            Tweet(tweet, onLikeClicked = onLikeClicked)
//            DividerM3()
//        }
//    }
//}
//
//@Composable
//fun Tweet(
//    post: Post,
//    onLikeClicked: (Post) -> Unit,
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Image(
//            painter = rememberImagePainter(
//                data = null,//post.creator.profilePictureUrl,
//                builder = {
//                    transformations(CircleCropTransformation())
//                    error(R.drawable.default_profile_picture)
//                },
//            ),
//            modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape),
//            contentDescription = null,
//        )
//        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
////            Username(
////                username = post.creator.username,
////                verified = post.creator.verified,
////                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
////            )
//            Text(
//                text = post.caption,
//                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
//            )
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(vertical = 8.dp),
//            ) {
//                val size = Modifier.size(22.dp)
//                LikeButton(
//                    like = post.liked,
//                    likes = post.likes,
//                    onToggle = { onLikeClicked(post) }
//                )
//                Icon(
//                    painter = rememberImagePainter(R.drawable.ic_bubble_icon),
//                    null,
//                    modifier = size
//                )
//                Icon(Icons.Outlined.Repeat, null, modifier = size)
//            }
//        }
//    }
//}
