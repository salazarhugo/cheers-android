package com.salazar.cheers.core.ui.components.image

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

@Composable
fun InspectionAwareComponent(
    @DrawableRes inspectionModePainter: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalInspectionMode.current) {
        true -> Image(
            painter = painterResource(id = inspectionModePainter),
            modifier = modifier,
            contentDescription = null,
        )
        false -> content()
    }
}
