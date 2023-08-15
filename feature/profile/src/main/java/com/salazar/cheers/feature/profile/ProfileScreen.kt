package com.salazar.cheers.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import com.salazar.cheers.core.ui.PartyItem
import com.salazar.cheers.core.ui.PrettyPanel
import com.salazar.cheers.core.ui.item.PostItem
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.core.util.numberFormatter
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.user.User
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
        val tabs = listOf(
            Icons.Outlined.ViewList,
            Icons.Default.GridView,
            Icons.Outlined.Celebration
        )
        val pagerState = rememberPagerState(
            pageCount = { tabs.size },
        )

        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                item {
                    ProfileItem(
                        user = uiState.user,
                        onWebsiteClick = onWebsiteClicked,
                        onEditProfileClicked = onEditProfileClicked,
                        onDrinkingStatsClick = onDrinkingStatsClick,
                        onStoryClick = onStoryClick,
                        onStatClicked = onStatClicked,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                stickyHeader {
                    val scope = rememberCoroutineScope()
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
//                            TabRowDefaults.Indicator(
//                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
//                            )
                        },
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ) {
                        tabs.forEachIndexed { index, icon ->
                            val selected = pagerState.currentPage == index
                            Tab(
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (selected) MaterialTheme.colorScheme.onBackground else Color.LightGray
                                    )
                               },
                                selected = selected,
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
                        state = pagerState,
                    ) { page ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            when (page) {
                                0 -> PostTab(
                                    posts = posts,
                                    onCommentClick = onCommentClick,
                                    onPostClicked = onPostClicked,
                                    onPostLike = onPostLike,
                                    onPostMoreClicked = onPostMoreClicked,
                                )
                                1 -> uiState.parties?.forEach {
                                    PartyItem(
                                        party = it,
                                        onEventClicked = {},
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
fun PostTab(
    posts: List<Post>?,
    onPostClicked: (postId: String) -> Unit,
    onPostLike: (post: Post) -> Unit,
    onPostMoreClicked: (String, String) -> Unit,
    onCommentClick: (String) -> Unit,
) {
    if (posts == null)
        return

    if (posts.isEmpty())
        PrettyPanel(
            title = stringResource(id = R.string.profile),
            body = stringResource(id = R.string.profile_empty_post),
        )

    posts.forEach { postFeed ->
        PostItem(
            post = postFeed,
//            onPostClicked,
//            onPostLike = onPostLike,
//            onPostMoreClicked = onPostMoreClicked,
//            onCommentClick = onCommentClick,
        )
    }
}

@Composable
fun PartyList(
    parties: List<Party>?,
) {
    if (parties != null)
        LazyColumn {
            items(parties, key = { it.id }) { event ->
                PartyItem(
                    party = event,
                    onEventClicked = {},
                    onMoreClick = {},
                )
            }
        }
    else
        LoadingScreen()
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
            PostGridItem(
                post = post,
                onPostClicked = onPostClicked,
            )
        }
    }
}

@Composable
fun PostGridItem(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
) {
    if (post.photos.isEmpty()) return
    Box(
        modifier = Modifier.padding(1.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        val url =
            if (post.type == com.salazar.cheers.data.post.repository.PostType.VIDEO) post.videoThumbnailUrl else post.photos[0]
        com.salazar.cheers.core.share.ui.PrettyImage(
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

        if (post.type == com.salazar.cheers.data.post.repository.PostType.VIDEO)
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

    TopAppBar(
        title = {
            com.salazar.cheers.core.share.ui.Username(
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
                Icon(Icons.Default.Add, contentDescription = null)
//                Icon(painter = painterResource(id = R.drawable.ic_add_box_white), "")
            }
            IconButton(onClick = navigateToProfileMoreSheet) {
                Icon(Icons.Default.Menu, contentDescription = null)
//                Icon(painter = painterResource(id = R.drawable.ic_more_vert_icon), "")
            }
        }
    )
}

@Composable
fun ProfileStats(
    user: User,
    onStatClicked: (statName: String, username: String, verified: Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val items = listOf(
            Counter("Posts", user.postCount, null),
            Counter("Parties", user.followers),
            Counter("Friends", user.friendsCount),
        )

        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onStatClicked(item.name, user.username, user.verified)
                }
            ) {
                Text(
                    text = "${numberFormatter(value = item.value)} ${item.name}",
                    fontFamily = Roboto,
//                    fontSize = 18.sp,
                )
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
