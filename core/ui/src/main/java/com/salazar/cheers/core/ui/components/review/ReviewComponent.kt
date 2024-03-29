package com.salazar.cheers.core.ui.components.review

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun ReviewComponent(
    modifier: Modifier = Modifier,
) {
}

@ComponentPreviews
@Composable
private fun ReviewComponentPreview() {
    CheersPreview {
        ReviewComponent(
            modifier = Modifier,
        )
    }
}
