package com.softimpact.feature.passcode.passcode

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun PinLockDial(
    modifier: Modifier = Modifier,
    biometricEnabled: Boolean,
    onAddDigit: (Int) -> Unit,
    onRemoveLastDigit: () -> Unit,
    onFingerprintClick: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .wrapContentSize()
            .padding(bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..3).forEach {
                PinKeyItem(
                    onClick = {
                        onAddDigit(it)
                    }
                ) {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (4..6).forEach {
                PinKeyItem(
                    onClick = {
                        onAddDigit(it)
                    }
                ) {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (7..9).forEach {
                PinKeyItem(
                    onClick = {
                        onAddDigit(it)
                    }
                ) {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (biometricEnabled)
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onFingerprintClick()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    tint = Color.White,
                )
            else
                Spacer(Modifier.size(25.dp))

            PinKeyItem(
                onClick = {
                    onAddDigit(0)
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.Backspace,
                contentDescription = "Clear",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        onRemoveLastDigit()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                tint = Color.White,
            )
        }
    }
}

@Composable
fun LottieLoadingView(
    context: Context,
    file: String,
    modifier: Modifier = Modifier,
    iterations: Int = 10
) {
//    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(file))
//
//    LottieAnimation(
//        composition = composition,
//        modifier = modifier.defaultMinSize(300.dp),
//        iterations = iterations
//    )
}

@Composable
fun PinKeyItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.padding(8.dp),
    shape: Shape = CircleShape,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        shape = shape,
        color = backgroundColor,
        contentColor = Color.White,
        tonalElevation = elevation,
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onClick()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
    ) {
        ProvideTextStyle(
            MaterialTheme.typography.displayMedium
        ) {
            Box(
                modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
