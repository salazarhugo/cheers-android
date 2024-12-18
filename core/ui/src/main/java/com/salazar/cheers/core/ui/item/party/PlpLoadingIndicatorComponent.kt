package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PlpLoadingIndicatorComponent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            strokeWidth = 1.dp,
        )
    }
}

@ComponentPreviews
@Composable
private fun PlpLoadingIndicatorComponentPreview() {
    CheersPreview {
        PlpLoadingIndicatorComponent(
            modifier = Modifier.padding(16.dp),
        )
    }
}
