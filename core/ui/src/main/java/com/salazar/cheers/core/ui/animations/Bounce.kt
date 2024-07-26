package com.salazar.cheers.core.ui.animations

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.salazar.cheers.core.ui.extensions.noRippleClickable

enum class BounceState { Pressed, Released }

@Composable
fun Bounce(
    modifier: Modifier = Modifier,
    hapticEnabled: Boolean = true,
    onBounce: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var currentState: BounceState by remember { mutableStateOf(BounceState.Released) }
    val transition = updateTransition(targetState = currentState, label = "animation")
    val scale: Float by transition.animateFloat(
        transitionSpec = { spring(stiffness = 900f) }, label = ""
    ) { state ->

        if (state == BounceState.Pressed) {
            0.90f
        } else {
            1f
        }
    }

    // Basic compose Box Layout
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .noRippleClickable {
                    if (hapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onBounce()
                }
//                .pointerInput(Unit) {
//                    detectTapGestures(onPress = {
//                        currentState = BounceState.Pressed
//                        val wasConsumedByOtherGesture = !tryAwaitRelease()
//                        currentState = BounceState.Released
//                        if (wasConsumedByOtherGesture)
//                            return@detectTapGestures
//                    })
//                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            content = content,
        )
    }
}