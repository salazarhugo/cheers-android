package com.salazar.cheers.core.ui.annotations

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    uiMode =  Configuration.UI_MODE_NIGHT_NO,
    apiLevel = 33,
)
@Preview(
    showBackground = true,
    uiMode =  Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 33,
)
annotation class LightAndDarkPreviews