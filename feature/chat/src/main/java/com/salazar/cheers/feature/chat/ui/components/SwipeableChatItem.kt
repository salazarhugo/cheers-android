package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.salazar.cheers.core.ui.theme.GreenGoogle


@Composable
fun SwipeableChatItem(
    modifier: Modifier = Modifier,
    state: SwipeToDismissBoxState,
    content: @Composable RowScope.() -> Unit,
) {
    SwipeToDismissBox(
        state = state,
        modifier = modifier
            .padding(vertical = Dp(1f)),
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        backgroundContent = {
            val direction = state.dismissDirection

            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> GreenGoogle
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                else -> { GreenGoogle }
            }

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> {Alignment.CenterEnd }
            }

            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Outlined.Archive
                SwipeToDismissBoxValue.EndToStart -> Icons.Outlined.PushPin
                else -> { Icons.Outlined.Archive }
            }

            val scale by animateFloatAsState(
                if (state.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f, label = ""
            )

            val haptic = LocalHapticFeedback.current
            if (state.targetValue != SwipeToDismissBoxValue.Settled)
                LaunchedEffect(Unit) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = Dp(20f)),
                contentAlignment = alignment,
            ) {
                Icon(
                    imageVector = icon,
                    modifier = Modifier.scale(scale),
                    contentDescription = null,
                )
            }
        },
        content = content,
    )
}
