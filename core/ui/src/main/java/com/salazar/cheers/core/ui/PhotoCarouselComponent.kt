package com.salazar.cheers.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.ui.PrettyImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PhotoCarouselComponent(
    photos: List<String>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState {
        photos.size
    },
    onPostClick: () -> Unit = {},
    onDoubleTap: () -> Unit = {},
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            var showLikeOverlay by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            val state = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)

                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight

                val maxX = extraWidth / 2
                val maxY = extraHeight / 2

                offset = Offset(
                    x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY),
                )
            }

            // Reset the image when released
            LaunchedEffect(state.isTransformInProgress) {
                if (!state.isTransformInProgress) {
                    scale = 1f
                    offset = Offset.Zero
                }
            }
            PrettyImage(
                data = photos[page],
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onDoubleClick = {
                            onDoubleTap()
                            scope.launch {
                                showLikeOverlay = true
                                delay(500)
                                showLikeOverlay = false
                            }
                        },
                        onClick = onPostClick,
                    )
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(
                        state = state,
                        canPan = { false },
                    )
            )
            AnimatedVisibility(
                visible = showLikeOverlay,
                enter = scaleIn(),
                exit = fadeOut(),
            ) {
                Image(
                    modifier = Modifier.size(90.dp),
                    painter = painterResource(id = R.drawable.ic_cheers_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White),
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun PhotoCarouselComponentPreview() {
    CheersPreview {
        PhotoCarouselComponent(
            photos = listOf("", "", ""),
            modifier = Modifier,
        )
    }
}