package com.salazar.cheers.feature.parties.myparties

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun MyPartiesTopBar(
    onBackPressed: () -> Unit,
) {
    Toolbar(
        onBackPressed = onBackPressed,
        title = stringResource(id = com.salazar.cheers.core.ui.R.string.myparties),
    )
}
