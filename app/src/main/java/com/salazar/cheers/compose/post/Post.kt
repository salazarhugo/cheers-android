package com.salazar.cheers.compose.post

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

@Composable
fun PostView(
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
            onPostClicked = { onPostClicked(post.id) },
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

