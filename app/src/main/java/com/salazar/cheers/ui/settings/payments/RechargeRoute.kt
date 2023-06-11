package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.util.Utils.getActivity
import kotlinx.coroutines.delay

@Composable
fun RechargeRoute(
    rechargeViewModel: RechargeViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by rechargeViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val recharges = uiState.skuDetails


    LaunchedEffect(Unit) {
        delay(1000)
        rechargeViewModel.refreshSkuDetails()
    }

    if (recharges == null)
        com.salazar.cheers.core.share.ui.LoadingScreen()
    else
        RechargeScreen(
            onRecharge = {
                val activity = context.getActivity()
                if (activity != null) {
                    rechargeViewModel.onProductClick(it, activity = activity)
                    rechargeViewModel.updateIsLoading(true)
                }
            },
            recharges = recharges,
            onBackPressed = { navActions.navigateBack() },
            coins = uiState.coins
        )
}

