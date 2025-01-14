package com.salazar.cheers.feature.settings.recharge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.shared.util.LocalActivity
import kotlinx.coroutines.delay

@Composable
fun RechargeRoute(
    rechargeViewModel: RechargeViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by rechargeViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val recharges = uiState.productDetails


    LaunchedEffect(Unit) {
        delay(1000)
        rechargeViewModel.refreshSkuDetails()
    }

    if (recharges == null) {
        LoadingScreen()
    } else {
        RechargeScreen(
            onRecharge = {
                rechargeViewModel.onProductClick(it, activity = activity)
                rechargeViewModel.updateIsLoading(true)
            },
            recharges = recharges,
            onBackPressed = navigateBack,
            coins = uiState.coins
        )
    }
}

