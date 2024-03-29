package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.MediaCarouselComponent
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PostMedia(
    medias: List<Media>,
    modifier: Modifier = Modifier,
    videoUrl: String? = null,
    onDoubleTap: () -> Unit = {},
    onPostClick: () -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        pageCount = { medias.size },
    ),
) {
    if (!videoUrl.isNullOrBlank() && medias.isEmpty())
        return

    Column(
        modifier = modifier,
    ) {
        MediaCarouselComponent(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)),
            medias = medias,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pagerState = pagerState,
            onPostClick = onPostClick,
            onDoubleTap = onDoubleTap,
        )

        if (medias.size > 1) {
            HorizontalPagerIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                pageCount = medias.size,
                pagerState = pagerState,
                activeColor = MaterialTheme.colorScheme.primary,
            )
        }
//        if (post.tagUsers.isNotEmpty())
//            InThisPhotoAnnotation(modifier = Modifier.align(Alignment.BottomStart))
    }
}


@ComponentPreviews
@Composable
private fun PostBodyPreview() {
    CheersPreview {
        PostMedia(
            medias = listOf(),
            modifier = Modifier,
        )
    }
}