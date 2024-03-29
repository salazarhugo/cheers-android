@file:OptIn(ExperimentalMaterial3Api::class)

package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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


@Composable
fun SwipeableMessage(
    state: SwipeToDismissBoxState,
    content: @Composable RowScope.() -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    SwipeToDismissBox(
        state = state,
        modifier = Modifier
            .padding(vertical = Dp(1f)),
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val alignment = Alignment.CenterEnd

            val scale by animateFloatAsState(
                if (state.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f, label = "",
            )

            if (state.targetValue != SwipeToDismissBoxValue.Settled) {
                LaunchedEffect(Unit) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dp(20f)),
                    contentAlignment = alignment
                ) {
                    Icon(
                        Icons.Outlined.Reply,
                        modifier = Modifier.scale(scale),
                        contentDescription = null,
                    )
                }
            }
        },
        content = content,
    )
}