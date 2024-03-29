package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.theme.BlueCheers


@Composable
fun ChatPresenceIndicator(
    isPresent: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = BlueCheers

    AnimatedVisibility(visible = isPresent) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 0.dp,
            modifier = modifier
                .clip(CircleShape)
                .size(16.dp)
                .drawBehind {
                    drawCircle(color = color)
                },
            content = {},
        )
    }
}

@Preview
@Composable
private fun ChatPresenceIndicatorPreview() {
    CheersPreview {
        ChatPresenceIndicator(
            isPresent = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}
