package com.salazar.cheers.components

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressIndicatorM3(
    modifier: Modifier = Modifier
) {
//     TODO (M3): No CircularProgressIndicator, replace when available
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        strokeWidth = 1.dp,
    )
}
