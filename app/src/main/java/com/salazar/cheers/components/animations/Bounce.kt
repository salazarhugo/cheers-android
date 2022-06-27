package com.salazar.cheers.components.animations

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

enum class BounceState { Pressed, Released }

@Composable
fun Bounce(
    onBounce: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
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
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        currentState = BounceState.Pressed
                        tryAwaitRelease()
                        currentState = BounceState.Released
                        onBounce()
                    })
                }.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            content()
        }
    }
}