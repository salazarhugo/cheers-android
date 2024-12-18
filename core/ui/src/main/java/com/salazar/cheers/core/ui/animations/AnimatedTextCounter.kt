package com.salazar.cheers.core.ui.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.salazar.cheers.core.util.numberFormatter


@Composable
fun AnimatedTextCounter(
    targetState: String,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedContentScope.(String) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = targetState,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = "",
        content = content,
    )
}

@Composable
fun AnimatedCounter(
    targetState: Int,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedContentScope.(Int) -> Unit,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = targetState,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = "",
        content = content,
    )
}

@Composable
fun AnimatedIntCounter(
    targetState: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    AnimatedCounter(
        modifier = modifier,
        targetState = targetState,
    ) { targetCount ->
        Text(
            text = numberFormatter(value = targetCount),
            style = style,
        )
    }
}
