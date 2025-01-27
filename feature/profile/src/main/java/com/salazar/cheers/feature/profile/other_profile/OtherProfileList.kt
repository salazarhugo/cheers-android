package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import com.salazar.cheers.core.ui.ProfileBannerAndAvatar
import com.salazar.cheers.core.ui.components.favorite_drink.FavoriteDrinkComponent
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.profileCarousel
import com.salazar.cheers.feature.profile.ProfileBody
import kotlinx.coroutines.launch


@Composable
fun OtherProfileList(
    user: User,
    posts: List<Post>?,
    parties: List<Party>?,
    state: LazyListState,
    modifier: Modifier = Modifier,
    onOtherProfileUIAction: (OtherProfileUIAction) -> Unit,
) {
    val userID = user.id
    val tabs = listOf(
        Icons.Outlined.SportsBar,
        Icons.Outlined.Celebration
    )
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
    )

    LazyColumn(
        modifier = modifier,
        state = state,
    ) {
        profileCarousel(
            user = user,
        )

        item {
            Column {
                ProfileBody(
                    user = user,
                    onFriendsClick = {
                        onOtherProfileUIAction(OtherProfileUIAction.OnFriendListClick)
                    },
                    onWebsiteClick = {},
                )
                HeaderButtons(
                    friend = user.friend,
                    requested = user.requested,
                    hasRequestedViewer = user.hasRequestedViewer,
                    onSendFriendRequest = {
                        onOtherProfileUIAction(OtherProfileUIAction.OnSendFriendRequest(userID))
                    },
                    onCancelFriendRequest = {
                        onOtherProfileUIAction(OtherProfileUIAction.OnCancelFriendRequest(userID))
                    },
                    onAcceptFriendRequest = {
                        onOtherProfileUIAction(OtherProfileUIAction.OnAcceptFriendRequest(userID))
                    },
                    onMessageClick = {
                        onOtherProfileUIAction(OtherProfileUIAction.OnSendMessageClick)
                    },
                )
            }
        }

        if (user.friend || user.isBusinessAccount) {
            profileTabs(
                tabs = tabs,
                pagerState = pagerState,
            )
            postsAndParties(
                posts = posts,
                parties = parties,
                pagerState = pagerState,
                onLikeClick =  {
                    onOtherProfileUIAction(OtherProfileUIAction.OnLikeClick(it))
                },
                onUserClick = {
                    onOtherProfileUIAction(OtherProfileUIAction.OnUserClick(it))
                },
                onCommentClick = {
                    onOtherProfileUIAction(OtherProfileUIAction.OnCommentClick(it))
                },
                onLikeCountClick = {
                    onOtherProfileUIAction(OtherProfileUIAction.OnLikeCountClick(it))
                }
            )
        }
    }
}

fun LazyListScope.avatar(
    drink: Drink?,
    avatar: String?,
    banner: String?,
    username: String,
    verified: Boolean,
) {
    item {
        ProfileBannerAndAvatar(
            modifier = Modifier.padding(16.dp),
            banner = banner,
            avatar = avatar,
            name = username,
            content = {
                if (drink != null) {
                    FavoriteDrinkComponent(drink = drink)
                }
            }
        )
    }
}

fun LazyListScope.profileTabs(
    tabs: List<ImageVector>,
    pagerState: PagerState,
) {
    stickyHeader {
        val scope = rememberCoroutineScope()

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
}

fun LazyListScope.postsAndParties(
    posts: List<Post>?,
    parties: List<Party>?,
    pagerState: PagerState,
    onLikeClick: (Post) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onLikeCountClick: (String) -> Unit = {},
) {
    item {
        HorizontalPager(
            state = pagerState,
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (page) {
                    0 -> posts?.forEach { post ->
                        PostComponent(
                            post = post,
                            onLikeClick = {
                                onLikeClick(post)
                            },
                            onCommentClick = {
                                onCommentClick(post.id)
                            },
                            onLikeCountClick = {
                                onLikeCountClick(post.id)
                            },
                            onUserClick = onUserClick,
                        )
                    }

                    1 -> parties?.forEach { party ->
                        PartyItem(
                            party = party,
                            onClick = {},
                            onMoreClick = {},
                        )
                    }

                    2 -> FunctionalityNotAvailablePanel()
                }
            }
        }
    }
}
