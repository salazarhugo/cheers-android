package com.salazar.cheers.core.ui.components.circular_progress

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun CircularProgressComponent(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = 1.dp,
    )
}

@ComponentPreviews
@Composable
private fun CircularProgressComponentPreview() {
    CheersPreview {
        CircularProgressComponent(
            modifier = Modifier.padding(16.dp),
        )
    }
}
