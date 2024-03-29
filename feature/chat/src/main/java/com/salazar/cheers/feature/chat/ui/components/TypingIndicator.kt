package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
internal fun TypingIndicator(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        TypingIndicatorAnimatedDot(
            0 * DotAnimationDurationMillis,
        )
        TypingIndicatorAnimatedDot(
            1 * DotAnimationDurationMillis,
        )
        TypingIndicatorAnimatedDot(
            2 * DotAnimationDurationMillis,
        )
    }
}

@ComponentPreviews
@Composable
private fun TypingIndicatorPreview() {
    CheersPreview {
        TypingIndicator(
            modifier = Modifier.padding(16.dp),
        )
    }
}