package com.salazar.cheers.core.ui.modifier

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.theme.nightClubColors

@Composable
fun Modifier.animatedBorder(
    shape: Shape = RectangleShape,
    borderWidth: Dp = 1.dp,
    initialColor: Color,
    targetColor: Color,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val borderColor by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    return this
        .border(width = borderWidth, color = borderColor, shape = shape)
}

@Composable
fun AnimatedBorder(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    borderWidth: Dp = 1.dp,
    colors: List<Color> = nightClubColors,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ), label = ""
    )

    val brush = Brush.sweepGradient(colors = colors)

    Surface(
        modifier = modifier,
        shape = shape,
    ) {
        Surface(
            modifier = Modifier
                .clipToBounds()
                .fillMaxWidth()
                .padding(borderWidth)
                .drawWithContent {
                    rotate(angle) {
                        drawCircle(
                            brush = brush,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                },
            shape = shape,
            content = content,
        )
    }
}
