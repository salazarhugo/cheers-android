package com.salazar.cheers.ui.compose.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.compose.video.VideoPlayer

@Composable
fun PostBody(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
    onLike: (post: Post) -> Unit,
    pagerState: PagerState = rememberPagerState(),
) {
    Box {
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

