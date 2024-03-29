package com.salazar.cheers.core.ui.components.record_button

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun RecordButtonComponent(
    modifier: Modifier = Modifier,
    isRecording: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color = when (isRecording) {
        true -> Color.Red
        false -> MaterialTheme.colorScheme.primary
    }

    val borderColor = when (isRecording) {
        true -> Color.Red
        false -> MaterialTheme.colorScheme.onBackground
    }

    val padding = when (isRecording) {
        true -> 22.dp
        false -> 7.dp
    }

    Bounce(
        modifier = modifier,
        onBounce =  onClick,
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 0.dp,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
                .border(4.dp, borderColor, CircleShape)
                .padding(padding)
                .drawBehind {
                    if (isRecording) {
                        drawRoundRect(color = color, cornerRadius = CornerRadius(16f))
                    } else {
                        drawCircle(color = color)
                    }
                }
        ) {}
    }
}

@ComponentPreviews
@Composable
private fun RecordButtonComponentPreview() {
    CheersPreview {
        RecordButtonComponent(
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun RecordButtonComponentPreviewRecording() {
    CheersPreview {
        RecordButtonComponent(
            isRecording = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}
