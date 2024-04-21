package com.salazar.cheers.feature.profile.cheerscode

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun CheerscodeTopBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    Toolbar(
        modifier = modifier,
        title = "My Cheerscode",
        onBackPressed = onBackPressed,
        actions = {},
    )
}

@ComponentPreviews
@Composable
private fun CheerscodeTopBarPreview() {
    CheersPreview {
        CheerscodeTopBar()
    }
}
