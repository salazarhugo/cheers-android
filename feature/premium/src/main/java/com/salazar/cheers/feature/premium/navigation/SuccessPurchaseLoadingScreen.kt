package com.salazar.cheers.feature.premium.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.ui.LoadingScreen

@Composable
fun SuccessPurchaseLoadingScreen(
) {
    LoadingScreen(
        modifier = Modifier,
    )
}

@Preview
@Composable
private fun SuccessPurchaseLoadingScreenPreview() {
    CheersPreview {
        SuccessPurchaseLoadingScreen()
    }
}
