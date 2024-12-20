package com.salazar.cheers.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.salazar.cheers.core.ui.extensions.noRippleClickable

fun Modifier.clickableNullable(callback: (() -> Unit)?): Modifier {
    return this.then(
        if (callback != null) {
            Modifier.noRippleClickable(onClick = callback)
        } else Modifier
    )
}

fun Modifier.carousel(pageOffset: Float): Modifier {
    return this.graphicsLayer {
        lerp(
            start = 0.85f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        ).also { scale ->
            scaleX = scale
            scaleY = scale
        }
        alpha = lerp(
            start = 0.5f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )
    }
}