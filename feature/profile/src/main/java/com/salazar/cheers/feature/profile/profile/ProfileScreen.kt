package com.salazar.cheers.feature.profile.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import com.salazar.cheers.core.ui.PrettyPanel
import com.salazar.cheers.core.ui.ProfileBannerAndAvatar
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.login_message.LoginMessageScreen
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.ui.PrettyImage
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.user.User
import com.salazar.cheers.feature.profile.ProfileItem
import com.salazar.cheers.feature.profile.ProfileTopBar
import com.salazar.cheers.feature.profile.R
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    navigateToSignIn: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
    navigateToProfileMoreSheet: (String) -> Unit = {},
    onProfileUIAction: (ProfileUIAction) -> Unit = {},
) {
    when (uiState) {
        is ProfileUiState.NoAccount -> {
            LoginMessageScreen(
                onSignInClick = navigateToSignIn,
                onRegisterClick = navigateToSignUp,
            )
        }
        is ProfileUiState.HasUser -> Profile(
            uiState = uiState,
            navigateToProfileMoreSheet = navigateToProfileMoreSheet,
            onProfileUIAction = onProfileUIAction,
        )
    }
}

@Composable
fun Profile(
    uiState: ProfileUiState.HasUser,
    navigateToProfileMoreSheet: (String) -> Unit,
    onProfileUIAction: (ProfileUIAction) -> Unit,
) {
    val user = uiState.user
    val state = rememberRefreshLayoutState()
    val scope  = rememberCoroutineScope()
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            scope.launch {
                state.finishRefresh(true)
            }
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                username = user.username,
                verified = user.verified,
                onBackPressed = { onProfileUIAction(ProfileUIAction.OnBackPressed) },
                onMenuClick = { navigateToProfileMoreSheet(user.username) },
            )
        }
    ) { insets ->
        PullToRefreshComponent(
            state = state,
            onRefresh = { onProfileUIAction(ProfileUIAction.OnSwipeRefresh) },
            modifier = Modifier.padding(top = insets.calculateTopPadding()),
        ) {
            ProfileList(
                user = user,
                posts = uiState.posts,
                parties = uiState.parties,
                onProfileUIAction = onProfileUIAction
            )
        }
    }
}

@Composable
fun ProfileList(
    user: User,
    posts: List<Post>?,
    parties: List<Party>?,
    onProfileUIAction: (ProfileUIAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val tabs = listOf(
        Icons.Outlined.ViewList,
        Icons.Default.GridView,
        Icons.Outlined.Celebration
    )
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
    )

    LazyColumn(
        state = listState,
    ) {

        item {
            ProfileBannerAndAvatar(
                modifier = Modifier.padding(16.dp),
                banner = user.banner,
                avatar = user.picture,
                content = {
                    OutlinedButton(
                        onClick = { onProfileUIAction(ProfileUIAction.OnEditProfileClick) },
                    ) {
                        Text(
                            text = stringResource(id = R.string.edit_profile),
                        )
                    }
                }
            )
        }
        item {
            val uriHandler = LocalUriHandler.current
            ProfileItem(
                user = user,
                onWebsiteClick = { website ->
                    var url = website
                    if (!url.startsWith("https://"))
                        url = "https://$url"
                    uriHandler.openUri(url)
                },
                onStatClicked = {
                    onProfileUIAction(ProfileUIAction.OnFriendListClick)
                },
            )
        }

        stickyHeader {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
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
                        )
                        1 -> parties?.forEach {
                            PartyItem(
                                party = it,
                                onPartyClicked = {},
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

@Composable
fun PostTab(
    posts: List<Post>?,
) {
    if (posts == null)
        return

    if (posts.isEmpty())
        PrettyPanel(
            title = stringResource(id = R.string.profile),
            body = stringResource(id = R.string.profile_empty_post),
        )

    posts.forEach { postFeed ->
        PostComponent(
            post = postFeed,
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
                    onPartyClicked = {},
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


//@Composable
//fun Tweets(
//    tweets: List<Post>,
//    onLikeClicked: (Post) -> Unit
//) {
//    LazyColumn(Modifier.height(800.dp)) {
//        items(tweets) { tweet ->
//            Tweet(tweet, onLikeClicked = onLikeClicked)
//            Divider()
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

@ScreenPreviews
@Composable
private fun ProfileScreenPreview() {
    CheersPreview {
//        ProfileScreen(
//            modifier = Modifier,
//        )
    }
}
