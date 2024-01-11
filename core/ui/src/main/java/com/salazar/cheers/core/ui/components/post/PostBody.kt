package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.PhotoCarouselComponent
import com.salazar.cheers.core.ui.VideoPlayer
import com.salazar.cheers.data.post.repository.Post

@Composable
fun PostBody(
    post: Post,
    onPostClick: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        pageCount = { post.photos.size },
    ),
) {
    if (post.videoUrl.isBlank() && post.photos.isEmpty())
        return

    Box(
        modifier = modifier,
    ) {
        if (post.videoUrl.isNotBlank()) {
            VideoPlayer(
                uri = post.videoUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 5f)
            )
        }
        else if (post.photos.isNotEmpty()) {
            PhotoCarouselComponent(
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                photos = post.photos,
                pagerState = pagerState,
                onPostClick = {
                    onPostClick(post.id)
                },
                onDoubleTap = onDoubleTap,
            )
        }
//        if (post.tagUsers.isNotEmpty())
//            InThisPhotoAnnotation(modifier = Modifier.align(Alignment.BottomStart))
    }
}

