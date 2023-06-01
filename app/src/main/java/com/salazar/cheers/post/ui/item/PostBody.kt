package com.salazar.cheers.post.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.post.ui.PhotoCarousel
import com.salazar.cheers.ui.compose.video.VideoPlayer

@Composable
fun PostBody(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
    onLike: (post: Post) -> Unit,
    pagerState: PagerState = rememberPagerState(),
) {
    if (post.videoUrl.isBlank() && post.photos.isEmpty())
        return

    Box(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        if (post.videoUrl.isNotBlank())
            VideoPlayer(
                uri = post.videoUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 5f)
            )
        else if (post.photos.isNotEmpty())
            PhotoCarousel(
                modifier = modifier,
                photos = post.photos,
                pagerState = pagerState,
            ) { onPostClicked(post.id) }
//        if (post.tagUsers.isNotEmpty())
//            InThisPhotoAnnotation(modifier = Modifier.align(Alignment.BottomStart))
    }
}

