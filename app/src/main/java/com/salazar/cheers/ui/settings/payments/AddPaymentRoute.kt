package com.salazar.cheers.ui.settings.payments

//import com.stripe.android.view.CardInputWidget
//
//@Composable
//fun AddPaymentMethod(
//    addPaymentViewModel: AddPaymentViewModel,
//    navActions: CheersNavigationActions,
//    onAddCard: () -> Unit,
//) {
//    val uiState by addPaymentViewModel.uiState.collectAsStateWithLifecycle()
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
