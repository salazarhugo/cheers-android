package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.components.circular_progress.CircularProgressComponent

@Composable
fun ButtonWithLoading(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    isLoading: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: ImageVector? = null,
) {
    Button(
        shape = shape,
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading && enabled,
    ) {
        if (isLoading) {
            CircularProgressComponent(
                modifier = Modifier
                    .size(ButtonDefaults.IconSize)
                    .align(Alignment.CenterVertically),
            )
        }
        else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
                Text(text = text)
            }
        }
    }
}

