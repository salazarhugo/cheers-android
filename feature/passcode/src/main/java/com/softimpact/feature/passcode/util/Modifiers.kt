package com.softimpact.feature.passcode.util

import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
