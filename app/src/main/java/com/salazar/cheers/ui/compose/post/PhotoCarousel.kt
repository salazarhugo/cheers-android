package com.salazar.cheers.ui.compose.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.salazar.cheers.ui.compose.utils.PrettyImage

@Composable
fun PhotoCarousel(
    modifier: Modifier = Modifier,
    photos: List<String>,
    pagerState: PagerState,
    onPostClick: () -> Unit,
) {
    HorizontalPager(
        count = photos.size,
        state = pagerState,
    ) { page ->
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale *= zoomChange
            offset += offsetChange
        }

        PrettyImage(
            data = photos[page],
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state)
                .clickable { onPostClick() }
        )
    }
}

