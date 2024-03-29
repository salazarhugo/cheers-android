package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun ErrorMessage(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    if (errorMessage.isNullOrBlank()) return

    Surface(
        modifier = modifier.padding(paddingValues = paddingValues),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = errorMessage,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}


@ComponentPreviews
@Composable
private fun ErrorMessagePreview() {
    CheersPreview {
        ErrorMessage(
            errorMessage = "Could not create account",
            modifier = Modifier,
        )
    }
}
