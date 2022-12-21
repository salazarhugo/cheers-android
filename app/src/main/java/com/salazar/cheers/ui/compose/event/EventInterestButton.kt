package com.salazar.cheers.ui.compose.event

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun EventInterestButton(
    modifier: Modifier = Modifier,
    interested: Boolean,
    onInterestedToggle: () -> Unit,
) {
    val icon = if (interested) Icons.Rounded.Star else Icons.Rounded.StarBorder

    FilledTonalButton(
        onClick = onInterestedToggle,
        modifier = modifier,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Interested")
    }
}
