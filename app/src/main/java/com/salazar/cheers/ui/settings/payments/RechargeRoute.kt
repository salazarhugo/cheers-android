package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.util.Utils.getActivity
import kotlinx.coroutines.delay

@Composable
fun RechargeRoute(
    rechargeViewModel: RechargeViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by rechargeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val recharges = uiState.skuDetails


    LaunchedEffect(Unit) {
        delay(1000)
        rechargeViewModel.refreshSkuDetails()
    }

    if (recharges == null)
        LoadingScreen()
    else
        RechargeScreen(
            onRecharge = {
                val activity = context.getActivity()
                if (activity != null)
                    rechargeViewModel.onSkuClick(it, activity = activity)
//                rechargeViewModel.updateIsLoading(true)
            },
            recharges = recharges,
            onBackPressed = { navActions.navigateBack() },
            coins = uiState.coins
        )
}

