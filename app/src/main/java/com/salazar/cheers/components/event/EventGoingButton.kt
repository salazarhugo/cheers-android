package com.salazar.cheers.components.event

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun EventGoingButton(
    modifier: Modifier = Modifier,
    going: Boolean,
    onGoingToggle: () -> Unit,
) {
    val iconGoing = if (going) Icons.Rounded.Check else Icons.Outlined.HelpOutline
    if (going)
        FilledTonalButton(
            onClick = onGoingToggle,
            modifier = modifier,
        ) {
            Icon(iconGoing, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Going")
        }
    else
        FilledIconButton(
            onClick = onGoingToggle,
            modifier = modifier,
        ) {
            Row() {
                Icon(iconGoing, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Going")
            }
        }
}
