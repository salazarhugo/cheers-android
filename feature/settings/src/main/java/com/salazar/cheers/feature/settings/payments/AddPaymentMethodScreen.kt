package com.salazar.cheers.feature.settings.payments


//@Composable
//fun AddPaymentMethodScreenScreen(
//    uiState: PaymentUiState,
//    onBackPressed: () -> Unit,
//    onAddCard: (CardParams) -> Unit,
//    cardInputWidget: CardInputWidget,
//) {
//    Scaffold(
//        topBar = {
//            Toolbar(
//                onBackPressed = onBackPressed,
//                title = "Wallet",
//            )
//        },
//        backgroundColor = MaterialTheme.colorScheme.background,
//    ) {
//        Column() {
//            LazyColumn() {
//                items(uiState.sources) { source ->
//                    Card(card = source)
//                }
//            }
//            TextButton(
//                onClick = { /*TODO*/ },
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text("Add payment method")
//            }
//            AndroidView(
//                modifier = Modifier.padding(16.dp),
//                factory = { cardInputWidget },
//            ) {
//            }
//            ButtonWithLoading(
//                text = "Add Card",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                isLoading = uiState.isLoading,
//                onClick = {
//                    val cardParams = cardInputWidget.cardParams ?: return@ButtonWithLoading
//                    onAddCard(cardParams)
//                },
//            )
//        }
//    }
//}
//
//@Composable
//fun Card(card: Source) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { }
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        val brand = CardBrand.fromCode(card.brand)
//        Image(
//            painter = rememberImagePainter(data = brand.icon),
//            modifier = Modifier.size(40.dp),
//            contentDescription = null,
//        )
//        Spacer(Modifier.width(8.dp))
//        Text(
//            text = "•••• •••• •••• ${card.last4!!}",
//            style = MaterialTheme.typography.bodyMedium,
//        )
//    }
//}
