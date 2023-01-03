package com.salazar.cheers.ui.compose.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.ui.main.home.HomeUIAction

@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    onHomeUIAction: (HomeUIAction) -> Unit,
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
            createTime = post.createTime,
            picture = post.profilePictureUrl,
            locationName = post.locationName,
            onHeaderClicked = { onHomeUIAction(HomeUIAction.OnUserClick(it)) },
            onMoreClicked = { onHomeUIAction(HomeUIAction.OnPostMoreClick(post.id)) },
        )
        PostText(
            caption = post.caption,
            onUserClicked = { onHomeUIAction(HomeUIAction.OnUserClick(it)) },
            onPostClicked = { onHomeUIAction(HomeUIAction.OnPostClick(post.id)) },
        )
        PostBody(
            post = post,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)),
            onPostClicked = { onHomeUIAction(HomeUIAction.OnPostClick(post.id)) },
            onLike = { onHomeUIAction(HomeUIAction.OnLikeClick(it)) },
            pagerState = pagerState
        )
        PostFooter(
            post = post,
            pagerState = pagerState,
            onLike = { onHomeUIAction(HomeUIAction.OnLikeClick(it)) },
            onCommentClick = { onHomeUIAction(HomeUIAction.OnCommentClick(it)) },
            onShareClick = {  onHomeUIAction(HomeUIAction.OnShareClick(it)) },
        )
    }
}
