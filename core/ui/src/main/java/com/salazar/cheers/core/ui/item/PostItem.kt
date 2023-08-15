package com.salazar.cheers.core.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.PostText
import com.salazar.cheers.data.post.repository.Post

@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        pageCount = { post.photos.size },
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        PostHeader(
            post = post,
            public = post.privacy == Privacy.PUBLIC.name,
            onUserClick = {
//                onHomeUIAction(HomeUIAction.OnUserClick(it))
            },
            onMoreClicked = {
//                onHomeUIAction(HomeUIAction.OnPostMoreClick(post.id))
            },
        )
        PostText(
            caption = post.caption,
            onUserClicked = {
//                onHomeUIAction(HomeUIAction.OnUserClick(it))
            },
            onPostClicked = {
//                onHomeUIAction(HomeUIAction.OnPostClick(post.id))
            },
        )
        PostBody(
            post = post,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)),
            onPostClicked = {
//                onHomeUIAction(HomeUIAction.OnPostClick(post.id))
            },
            onLike = {
//                onHomeUIAction(HomeUIAction.OnLikeClick(it))
            },
            pagerState = pagerState
        )
        PostFooter(
            post = post,
            pagerState = pagerState,
            onLike = {
//                onHomeUIAction(HomeUIAction.OnLikeClick(it))
            },
            onCommentClick = {
//                onHomeUIAction(HomeUIAction.OnCommentClick(it))
            },
            onShareClick = {
//                onHomeUIAction(HomeUIAction.OnShareClick(it))
            },
        )
    }
}

