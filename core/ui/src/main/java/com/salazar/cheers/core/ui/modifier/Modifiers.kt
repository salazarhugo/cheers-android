package com.salazar.cheers.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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

fun Modifier.gradientBackground(
    angle: Float,
    colors: List<Color>,
) = then(Modifier.drawBehind {
    val angleRad = angle / 180f * PI
    val x = cos(angleRad).toFloat()
    val y = sin(angleRad).toFloat()

    val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
    val offset = center + Offset(x * radius, y * radius)

    val exactOffset = Offset(
        x = min(offset.x.coerceAtLeast(0f), size.width),
        y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
    )

    drawRect(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(size.width, size.height) - exactOffset,
            end = exactOffset
        ), size = size
    )
    blur(
        radius = 30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded
    )
})

fun Modifier.leftBorder(
    width: Dp,
    color: Color,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height),
        strokeWidth = width.toPx(),
    )
}