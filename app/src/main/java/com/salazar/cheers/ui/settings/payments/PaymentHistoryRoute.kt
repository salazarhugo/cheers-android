package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

@Composable
fun PaymentHistoryRoute(
    paymentHistoryViewModel: PaymentHistoryViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by paymentHistoryViewModel.uiState.collectAsState()

//    PaymentHistoryScreen(
//        onBackPressed = { navActions.navigateBack() },
//        payments = uiState.payments
//    )
}