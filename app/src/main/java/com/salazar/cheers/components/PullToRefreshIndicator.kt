package com.salazar.cheers.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Immutable
private data class SwipeRefreshIndicatorSizes(
    val size: Dp,
    val arcRadius: Dp,
    val strokeWidth: Dp,
    val arrowWidth: Dp,
    val arrowHeight: Dp,
)

/**
 * The default/normal size values for [SwipeRefreshIndicator].
 */
private val DefaultSizes = SwipeRefreshIndicatorSizes(
    size = 40.dp,
    arcRadius = 7.5.dp,
    strokeWidth = 2.5.dp,
    arrowWidth = 10.dp,
    arrowHeight = 5.dp,
)

/**
 * The 'large' size values for [SwipeRefreshIndicator].
 */
private val LargeSizes = SwipeRefreshIndicatorSizes(
    size = 56.dp,
    arcRadius = 11.dp,
    strokeWidth = 3.dp,
    arrowWidth = 12.dp,
    arrowHeight = 6.dp,
)

/**
 * Indicator composable which is typically used in conjunction with [SwipeRefresh].
 *
 * @param state The [SwipeRefreshState] passed into the [SwipeRefresh] `indicator` block.
 * @param modifier The modifier to apply to this layout.
 * @param fade Whether the arrow should fade in/out as it is scrolled in. Defaults to true.
 * @param scale Whether the indicator should scale up/down as it is scrolled in. Defaults to false.
 * @param arrowEnabled Whether an arrow should be drawn on the indicator. Defaults to true.
 * @param backgroundColor The color of the indicator background surface.
 * @param contentColor The color for the indicator's contents.
 * @param shape The shape of the indicator background surface. Defaults to [CircleShape].
 * @param largeIndication Whether the indicator should be 'large' or not. Defaults to false.
 * @param elevation The size of the shadow below the indicator.
 */
@Composable
fun PullRefreshIndicator(
    state: PullRefreshState,
    refreshTriggerDistance: Dp,
    modifier: Modifier = Modifier,
    scale: Boolean = false,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    refreshingOffset: Dp = 160.dp,
) {
    val indicatorRefreshTrigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
    val refreshingOffsetPx = with(LocalDensity.current) { refreshingOffset.toPx() }

    val slingshot = rememberUpdatedSlingshot(
        offsetY = state.indicatorOffset,
        maxOffsetY = indicatorRefreshTrigger,
        height = refreshTriggerDistance.value.toInt(),
    )

    var offset by remember { mutableStateOf(-200f) }

//     If the user is currently swiping, we use the 'slingshot' offset directly
    if (state.isPullInProgress) {
        offset = slingshot.offset.toFloat()
    }

    LaunchedEffect(state.isPullInProgress, state.isRefreshing) {
        // If there's no swipe currently in progress, animate to the correct resting position
        if (!state.isPullInProgress) {
            animate(
                initialValue = offset,
                targetValue = when {
                    state.isRefreshing -> refreshingOffsetPx
                    else -> -refreshTriggerDistance.value
                }
            ) { value, _ ->
                offset = value
            }
        }
    }

    Surface(
        modifier = modifier
            .size(size = refreshTriggerDistance)
            .graphicsLayer {
                // Translate the indicator according to the slingshot
                translationY = offset

                val scaleFraction = if (scale && !state.isRefreshing) {
                    val progress = offset / indicatorRefreshTrigger.coerceAtLeast(1f)

                    // We use LinearOutSlowInEasing to speed up the scale in
                    LinearOutSlowInEasing
                        .transform(progress)
                        .coerceIn(0f, 1f)
                } else 1f

                scaleX = scaleFraction
                scaleY = scaleFraction
            },
//        shape = shape,
        color = backgroundColor,
    ) {
        // This shows either an Image with CircularProgressPainter or a CircularProgressIndicator,
        // depending on refresh state
        Crossfade(
            targetState = state.isRefreshing,
            animationSpec = tween(durationMillis = CrossfadeDurationMs)
        ) { refreshing ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (refreshing) {
                    Text("Refreshing")
//                    val circleSize = (sizes.arcRadius + sizes.strokeWidth) * 2
//                    CircularProgressIndicator(
//                        color = contentColor,
//                        strokeWidth = sizes.strokeWidth,
//                        modifier = Modifier.size(circleSize),
//                    )
                } else {
//                    Image(
//                        painter = painter,
//                        contentDescription = "Refreshing"
//                    )
                }
            }
        }
    }
}

private const val CrossfadeDurationMs = 100
