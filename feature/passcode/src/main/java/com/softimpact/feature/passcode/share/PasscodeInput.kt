package com.softimpact.feature.passcode.share

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.theme.CheersBlueSecondary
import com.salazar.cheers.core.ui.theme.CheersTheme
import com.softimpact.feature.passcode.create.pinSize

@Composable
fun PasscodeInput(
    inputPin: String,
    error: Boolean = false,
    backgroundColor: Color = CheersBlueSecondary,
) {
    val currentDigitIndex = inputPin.length
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = CheersBlueSecondary,
        targetValue = BlueCheers,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Row {
        (0 until pinSize).forEach { i ->
            val shape = RoundedCornerShape(12.dp)
            val hasDigit = inputPin.length > i

            val borderColor = when (i) {
                currentDigitIndex -> color
                else -> CheersBlueSecondary
            }

            val borderWidth = when (i) {
                currentDigitIndex -> 2.dp
                else -> 0.dp
            }

            val animatedBackgroundColor = when (i) {
                currentDigitIndex -> color
                else -> backgroundColor
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(50.dp)
                    .height(70.dp)
                    .background(animatedBackgroundColor, shape)
                    .border(borderWidth, borderColor, shape),
                contentAlignment = Alignment.Center,
            ) {
                if (hasDigit) Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
fun PasscodeInputPreview() {
    CheersTheme {
        PasscodeInput(
            backgroundColor = Color.White,
            inputPin = "123456",
        )
    }
}
