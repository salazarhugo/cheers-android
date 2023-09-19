package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.ButtonWithLoading

@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ButtonWithLoading(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        text = text,
        shape = MaterialTheme.shapes.medium,
        isLoading = isLoading,
        onClick = onClick,
        enabled = enabled,
    )
}

