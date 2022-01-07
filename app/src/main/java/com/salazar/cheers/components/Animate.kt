package com.salazar.cheers.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.home.HomeUiState

@Composable
fun AnimatePost(uiState: HomeUiState.HasPosts, index: Int) {
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
        exit = slideOutHorizontally() + fadeOut()
    ) {
//            Post(uiState = uiState, index)
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
        enter = slideInVertically(
            initialOffsetY = { with(density) { 200.dp.roundToPx() } }
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
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
fun AnimateVisibilityFade(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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
