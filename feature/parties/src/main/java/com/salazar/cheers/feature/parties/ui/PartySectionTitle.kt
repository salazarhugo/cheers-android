package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PartySectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}

@ComponentPreviews
@Composable
private fun PartySectionTitlePreview() {
    CheersPreview {
        PartySectionTitle(
            text = "What to expect",
            modifier = Modifier.padding(16.dp),
        )
    }
}
