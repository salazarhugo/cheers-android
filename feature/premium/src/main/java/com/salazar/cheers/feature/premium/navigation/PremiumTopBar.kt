package com.salazar.cheers.feature.premium.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun PremiumTopBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    Toolbar(
        modifier = modifier,
        title = "Cheers Premium",
        onBackPressed = onBackPressed,
        actions = {},
    )
}

@ComponentPreviews
@Composable
private fun PremiumTopBarPreview() {
    CheersPreview {
        PremiumTopBar()
    }
}
