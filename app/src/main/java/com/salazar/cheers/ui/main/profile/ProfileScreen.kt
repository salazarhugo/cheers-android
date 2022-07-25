package com.salazar.cheers.ui.main.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.R
import com.salazar.cheers.components.FunctionalityNotAvailablePanel
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.post.PostBody
import com.salazar.cheers.components.post.PostFooter
import com.salazar.cheers.components.post.PostHeader
import com.salazar.cheers.components.post.PostText
import com.salazar.cheers.components.profile.ProfileHeader
import com.salazar.cheers.components.profile.ProfileText
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.components.utils.PrettyImage
import com.salazar.cheers.internal.*
import com.salazar.cheers.ui.main.event.Event
import com.salazar.cheers.ui.theme.Roboto
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSwipeRefresh: () -> Unit,
    onEditProfileClicked: () -> Unit,
    onDrinkingStatsClick: (String) -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    navigateToProfileMoreSheet: (String) -> Unit,
    onPostMoreClicked: (String, String) -> Unit,
    onWebsiteClicked: (String) -> Unit,
    onStoryClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit,
) {
    when (uiState) {
        is ProfileUiState.Loading -> LoadingScreen()
        is ProfileUiState.HasUser -> Profile(
            uiState = uiState,
            onEditProfileClicked = onEditProfileClicked,
            onPostClicked = onPostClicked,
            onStatClicked = onStatClicked,
            navigateToProfileMoreSheet = navigateToProfileMoreSheet,
            onWebsiteClicked = onWebsiteClicked,
            onDrinkingStatsClick = onDrinkingStatsClick,
            onSwipeRefresh = onSwipeRefresh,
            onPostLike = onPostLike,
            onPostMoreClicked = onPostMoreClicked,
            onStoryClick = onStoryClick,
            onCommentClick = onCommentClick,
        )
    }
}

@Composable
fun Profile(
    uiState: ProfileUiState.HasUser,
    onEditProfileClicked: () -> Unit,
    onDrinkingStatsClick: (String) -> Unit,
    onSwipeRefresh: () -> Unit,
    onPostMoreClicked: (String, String) -> Unit,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
    navigateToProfileMoreSheet: (String) -> Unit,
    onWebsiteClicked: (String) -> Unit,
    onStoryClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                uiState = uiState,
            ) { navigateToProfileMoreSheet(uiState.user.username) }
        }
    ) {
        val posts = uiState.posts
        val pagerState = rememberPagerState()
        val tabs = listOf(
            Icons.Outlined.ViewList,
            Icons.Default.GridView,
            Icons.Outlined.Celebration
        )

        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        ProfileHeader(
                            user = uiState.user,
                            onStatClicked = onStatClicked,
                            onStoryClick = onStoryClick
                        )
                        ProfileText(user = uiState.user, onWebsiteClicked = onWebsiteClicked)
                        ProfileButtons(
                            onEditProfileClicked = onEditProfileClicked,
                            onDrinkingStatsClick = { onDrinkingStatsClick(uiState.user.username) },
                        )
                    }
                }
                stickyHeader {
                    val scope = rememberCoroutineScope()
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ) {
                        tabs.forEachIndexed { index, icon ->
                            Tab(
                                icon = { Icon(icon, contentDescription = null) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                            )
                        }
                    }
                }

                item {
                    HorizontalPager(
                        count = tabs.size,
                        state = pagerState,
                    ) { page ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            when (page) {
                                0 -> posts?.forEach { postFeed ->
                                    Post(
                                        postFeed,
                                        onPostClicked,
                                        onPostLike = onPostLike,
                                        onPostMoreClicked = onPostMoreClicked,
                                        onCommentClick = onCommentClick,
                                    )
                                }
                                1 -> uiState.events?.forEach {
                                    Event(
                                        event = it,
                                        onEventClicked = {},
                                        onInterestedToggle = {},
                                        onGoingToggle = {},
                                        onMoreClick = {},
                                    )
                                }
                                2 -> FunctionalityNotAvailablePanel()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventList(
    events: List<Event>?,
) {
    if (events != null)
        LazyColumn {
            items(events, key = { it.id }) { event ->
                Event(
                    event = event,
                    onEventClicked = {},
                    onGoingToggle = {},
                    onInterestedToggle = {},
                    onMoreClick = {},
                )
            }
        }
    else
        LoadingScreen()
}

@Composable
fun ProfileButtons(
    onEditProfileClicked: () -> Unit,
    onDrinkingStatsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        FilledTonalButton(
            onClick = onEditProfileClicked,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(0.9f)
                .height(34.dp)
        ) {
            Text("Edit Profile")
        }
//        IconButton(onClick = onDrinkingStatsClick) {
//            Icon(Icons.Outlined.QueryStats, null)
//        }
    }
}

@Composable
fun Post(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onPostMoreClicked: (String, String) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState()

    PostHeader(
        username = post.username,
        verified = post.verified,
        beverage = Beverage.fromName(post.beverage),
        public = post.privacy == Privacy.PUBLIC.name,
        created = post.created,
        profilePictureUrl = post.profilePictureUrl,
        locationName = post.locationName,
        onHeaderClicked = {},
        onMoreClicked = { onPostMoreClicked(post.id, post.authorId) },
    )
    PostText(
        caption = post.caption,
        onUserClicked = {},
        onPostClicked = { onPostClicked(post.id) },
    )
    PostBody(
        post = post,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        onPostClicked = onPostClicked,
        onLike = {},
        pagerState = pagerState,
    )
    PostFooter(
        post,
        onLike = onPostLike,
        navigateToComments = {},
        pagerState = pagerState,
        onCommentClick = onCommentClick,
    )
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
        items(posts, key = { it.id }) { post ->
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
}

@Composable
fun ProfileStats(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val items = listOf(
            Counter("Posts", user.postCount, null),
            Counter("Followers", user.followers),
            Counter("Following", user.following),
        )

        items.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onStatClicked(item.name, user.username, user.verified)
                }
            ) {
                Text(
                    text = numberFormatter(value = item.value),
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                    fontSize = 18.sp,
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
