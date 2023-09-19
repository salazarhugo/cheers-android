package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
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
    dismissState: DismissState,
    content: @Composable () -> Unit,
) {
    SwipeToDismiss(
        state = dismissState,
        modifier = modifier
            .padding(vertical = Dp(1f)),
        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
//        dismissThresholds = {
//            FractionalThreshold(0.2f)
//        },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val color = when (direction) {
                DismissDirection.StartToEnd -> GreenGoogle
                DismissDirection.EndToStart -> androidx.compose.material3.MaterialTheme.colorScheme.primary
            }

            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }

            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Outlined.Archive
                DismissDirection.EndToStart -> Icons.Outlined.PushPin
            }

            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = ""
            )

            val haptic = LocalHapticFeedback.current
            if (dismissState.targetValue != DismissValue.Default)
                LaunchedEffect(Unit) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = Dp(20f)),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    modifier = Modifier.scale(scale),
                    contentDescription = null,
                )
            }
        },
        dismissContent = {
            Card(
//                elevation = animateDpAsState(
//                    if (dismissState.dismissDirection != null) 4.dp else 0.dp
//                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterVertically),
                content = {
                    content()
                },
            )
        },
    )
}
