package com.salazar.cheers.core.ui.animations

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.salazar.cheers.core.ui.R

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
    val CARDIAC_CYLCLE_MILLIS = 800

    Pulsating(durationMillis = CARDIAC_CYLCLE_MILLIS) {
        Image(
            modifier = Modifier.size(80.dp),
            painter = rememberAsyncImagePainter(R.drawable.ic_artboard_1cheers_logo_svg),
            contentDescription = null,
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
