package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.salazar.cheers.navigation.CheersNavigationActions
//import com.stripe.android.view.CardInputWidget
//
//@Composable
//fun AddPaymentMethod(
//    addPaymentViewModel: AddPaymentViewModel,
//    navActions: CheersNavigationActions,
//    onAddCard: () -> Unit,
//) {
//    val uiState by addPaymentViewModel.uiState.collectAsState()
//
//    if (uiState.sources.isNotEmpty())
//        onAddCard()
//
//    val context = LocalContext.current
//    val cardInputWidget = remember { CardInputWidget(context) }
//    val focusManager = LocalFocusManager.current
//
//    AddPaymentMethodScreenScreen(
//        uiState = uiState,
//        onBackPressed = { navActions.navigateBack() },
//        onAddCard = {
//            focusManager.clearFocus()
//            addPaymentViewModel.addCard(context = context, cardParams = it)
//        },
//        cardInputWidget = cardInputWidget
//    )
//
//}
//
