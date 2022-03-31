package com.salazar.cheers.components.share

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefreshState

/**
 * A custom indicator which displays a glow and progress indicator
 */
@Composable
fun GlowIndicator(
    swipeRefreshState: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
        Modifier
            .drawWithCache {
                onDrawBehind {
                    val distance = refreshTriggerDistance.toPx()
                    val progress = (swipeRefreshState.indicatorOffset / distance).coerceIn(0f, 1f)
                    // We draw a translucent glow
                    val brush = Brush.verticalGradient(
                        0f to color.copy(alpha = 0.45f),
                        1f to color.copy(alpha = 0f)
                    )
                    // And fade the glow in/out based on the swipe progress
                    drawRect(brush = brush, alpha = FastOutSlowInEasing.transform(progress))
                }
            }
            .fillMaxWidth()
            .height(72.dp)
            .offset(y = swipeRefreshState.indicatorOffset.dp)
    ) {
        if (swipeRefreshState.isRefreshing) {
            // If we're refreshing, show an indeterminate progress indicator
            LinearProgressIndicator(Modifier.fillMaxWidth())
        } else {
            // Otherwise we display a determinate progress indicator with the current swipe progress
            val trigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
            val progress = (swipeRefreshState.indicatorOffset / trigger).coerceIn(0f, 1f)

            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}