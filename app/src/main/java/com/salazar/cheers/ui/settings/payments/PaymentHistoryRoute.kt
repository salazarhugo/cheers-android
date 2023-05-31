package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

@Composable
fun PaymentHistoryRoute(
    paymentHistoryViewModel: PaymentHistoryViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by paymentHistoryViewModel.uiState.collectAsStateWithLifecycle()

//    PaymentHistoryScreen(
//        onBackPressed = { navActions.navigateBack() },
//        payments = uiState.payments
//    )
}