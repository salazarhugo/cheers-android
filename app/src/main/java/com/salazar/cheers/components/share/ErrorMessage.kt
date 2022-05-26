package com.salazar.cheers.components.share

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessage(
    errorMessage: String?,
    paddingValues: PaddingValues,
) {
    if (errorMessage == null || errorMessage.isBlank()) return

    Surface(
        modifier = Modifier.padding(paddingValues = paddingValues),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = errorMessage,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

