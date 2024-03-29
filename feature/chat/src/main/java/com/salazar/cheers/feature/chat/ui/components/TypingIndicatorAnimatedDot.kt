package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import kotlinx.coroutines.delay

@Composable
internal fun TypingIndicatorAnimatedDot(
    initialDelayMillis: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val alpha = remember { Animatable(0.5f) }

    LaunchedEffect(initialDelayMillis) {
        delay(initialDelayMillis.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = DotAnimationDurationMillis,
                    delayMillis = DotAnimationDurationMillis,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
        )
    }

    val dotColor: Color = color.copy(alpha = alpha.value)

    Box(
        modifier = modifier
            .background(dotColor, CircleShape)
            .size(5.dp),
    )
}

@ComponentPreviews
@Composable
private fun TypingIndicatorAnimationDotPreview() {
    CheersPreview {
        TypingIndicatorAnimatedDot(
            initialDelayMillis = 1000,
            modifier = Modifier.padding(16.dp),
        )
    }
}

const val DotAnimationDurationMillis: Int = 200
