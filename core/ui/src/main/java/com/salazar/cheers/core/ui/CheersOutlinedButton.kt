package com.salazar.cheers.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun CheersOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        shape = shape,
        modifier = modifier
            .height(34.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content,
    )
}

@ComponentPreviews
@Composable
private fun CheersOutlinedButtonPreview() {
    CheersPreview {
        CheersOutlinedButton(
            modifier = Modifier.padding(16.dp),
            onClick = {},
        ) {
            Text(text = "Cheers social")
        }
    }
}
