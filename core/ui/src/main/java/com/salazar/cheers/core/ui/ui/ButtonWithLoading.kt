package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ButtonWithLoading(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    isLoading: Boolean,
    valid: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        shape = shape,
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading && valid,
    ) {
        if (isLoading)
            CircularProgressIndicator(
                modifier = Modifier
                    .size(ButtonDefaults.IconSize)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 1.dp
            )
        else
            Text(text = text)
    }
}

