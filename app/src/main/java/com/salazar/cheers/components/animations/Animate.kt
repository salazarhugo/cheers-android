package com.salazar.cheers.components.animations

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R

@Composable
fun AnimatePost(
    content: @Composable () -> Unit,
) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val density = LocalDensity.current
    AnimatedVisibility(
        visibleState = state,
        enter = expandVertically(),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
    ) {
        content()
    }
}

@Composable
fun AnimateMessage(content: @Composable () -> Unit) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val density = LocalDensity.current
    AnimatedVisibility(
        visibleState = state,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
    ) {
        content()
    }
}

@Composable
fun Animate(content: @Composable () -> Unit) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val density = LocalDensity.current
    AnimatedVisibility(
        visibleState = state,
        enter = slideInVertically(
            initialOffsetY = { with(density) { -400.dp.roundToPx() } }
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + fadeOut(),
    ) {
        content()
    }
}

@Composable
fun AnimatedLogo() {
    val value by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        )
    )
    val visible by remember { mutableStateOf(true) }
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
//        Canvas(modifier = Modifier, onDraw = {
//            glowPaint.setShadowLayer(
//                1f,
//                0f,
//                0f,
//                Color.WHITE
//            )
//            drawArc(glowPaint.color, 0f, 360f, true)
//        })
        Image(
            painter = rememberAsyncImagePainter(R.drawable.ic_artboard_1cheers_logo_svg),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .alpha(alpha = value)
        )
    }
}

@Composable
fun AnimateHeart(content: @Composable () -> Unit) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val density = LocalDensity.current
    AnimatedVisibility(
        visibleState = state,
        enter = slideInVertically(
            initialOffsetY = { with(density) { 40.dp.roundToPx() } }
        ),
        exit = slideOutVertically(),
    ) {
        content()
    }
}

@Composable
fun AnimateVisibilityFade(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        modifier = modifier,
        visibleState = state,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        content()
    }
}
