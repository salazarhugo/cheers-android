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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.PostType
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import com.salazar.cheers.core.ui.PrettyPanel
import com.salazar.cheers.core.ui.ProfileHeaderCarousel
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.login_message.LoginMessageScreen
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.PullToRefreshComponent
import com.salazar.cheers.core.ui.components.pull_to_refresh.rememberRefreshLayoutState
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.PrettyImage
import com.salazar.cheers.feature.profile.ProfileBody
import com.salazar.cheers.feature.profile.ProfileMoreBottomSheet
import com.salazar.cheers.feature.profile.ProfileSheetUIAction
import com.salazar.cheers.feature.profile.R
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    navigateToSignIn: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
    onProfileUIAction: (ProfileUIAction) -> Unit = {},
    onProfileSheetUIAction: (ProfileSheetUIAction) -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    when (uiState) {
        is ProfileUiState.HasUser -> Profile(
            uiState = uiState,
            navigateToProfileMoreSheet = {
                showBottomSheet = true
            },
            onProfileUIAction = onProfileUIAction,
        )

        ProfileUiState.Loading -> {
            LoadingScreen()
        }

        ProfileUiState.NotSignIn -> {
            LoginMessageScreen(
                onSignInClick = navigateToSignIn,
                onRegisterClick = navigateToSignUp,
            )
        }
    }

    if (showBottomSheet) {
        ProfileMoreBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            onProfileSheetUIAction = { action ->
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showBottomSheet = false
                    onProfileSheetUIAction(action)
                }
            },
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
    val scope = rememberCoroutineScope()
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
                premium = user.premium,
                onBackPressed = { onProfileUIAction(ProfileUIAction.OnBackPressed) },
                onMenuClick = { navigateToProfileMoreSheet(user.username) },
                onEditClick = { onProfileUIAction(ProfileUIAction.OnEditProfileClick) },
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
        Icons.AutoMirrored.Outlined.ViewList,
        Icons.Default.GridView,
        Icons.Outlined.Celebration
    )
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
    )
    val drink = user.favouriteDrink

    LazyColumn(
        state = listState,
    ) {
        profileCarousel(
            user = user,
        )

        profileBody(
            user = user,
            onProfileUIAction = onProfileUIAction,
        )

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
                            onProfileUIAction = onProfileUIAction,
                        )

                        1 -> parties?.forEach {
                            PartyItem(
                                party = it,
                                onClick = {
                                    onProfileUIAction(ProfileUIAction.OnPartyClick(it))
                                },
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

private fun LazyListScope.profileCarousel(
    user: User,
) {
    item {
        ProfileHeaderCarousel(
            user = user,
        )
    }
}

private fun LazyListScope.profileBody(
    user: User,
    onProfileUIAction: (ProfileUIAction) -> Unit,
) {
    item {
        ProfileBody(
            user = user,
            onFriendsClick = {
                onProfileUIAction(ProfileUIAction.OnFriendListClick)
            },
            onWebsiteClick = {
                onProfileUIAction(ProfileUIAction.OnLinkClick(it))
            },
        )
    }

}

@Composable
fun PostTab(
    posts: List<Post>?,
    onProfileUIAction: (ProfileUIAction) -> Unit,
) {
    if (posts == null) return

    if (posts.isEmpty()) {
        PrettyPanel(
            title = stringResource(id = R.string.profile),
            body = stringResource(id = R.string.profile_empty_post),
        )
    }

    posts.forEach { postFeed ->
        PostComponent(
            post = postFeed,
            onMoreClick = {
                onProfileUIAction(ProfileUIAction.OnPostMoreClick(postFeed.id))
            },
            onDetailsClick = {
                onProfileUIAction(ProfileUIAction.OnPostDetailsClick(postFeed.id))
            },
            onUserClick = {
                onProfileUIAction(ProfileUIAction.OnUserClick(it))
            },
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
                    onClick = {},
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
            if (post.type == PostType.VIDEO) post.videoThumbnailUrl else post.photos[0]
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
//        val context = LocalContext.current
//        val username = it.arguments?.getString("username")!!
//        val clipboardManager = LocalClipboardManager.current
//        val scope = rememberCoroutineScope()
//
//        ProfileMoreBottomSheet(
//        )
