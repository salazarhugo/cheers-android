package com.salazar.cheers.ui.settings.payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.salazar.cheers.components.share.ButtonWithLoading
import com.salazar.cheers.components.share.Toolbar
import com.salazar.cheers.internal.Source
import com.stripe.android.model.CardBrand
import com.stripe.android.model.CardParams
import com.stripe.android.view.CardInputWidget


@Composable
fun AddPaymentMethodScreenScreen(
    uiState: PaymentUiState,
    onBackPressed: () -> Unit,
    onAddCard: (CardParams) -> Unit,
    cardInputWidget: CardInputWidget,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = onBackPressed,
                title = "Wallet",
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
    ) {
        Column() {
            LazyColumn() {
                items(uiState.sources) { source ->
                    Card(card = source)
                }
            }
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add payment method")
            }
            AndroidView(
                modifier = Modifier.padding(16.dp),
                factory = { cardInputWidget },
            ) {
            }
            ButtonWithLoading(
                text = "Add Card",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                isLoading = uiState.isLoading,
                onClick = {
                    val cardParams = cardInputWidget.cardParams ?: return@ButtonWithLoading
                    onAddCard(cardParams)
                },
            )
        }
    }
}

@Composable
fun Card(card: Source) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val brand = CardBrand.fromCode(card.brand)
        Image(
            painter = rememberImagePainter(data = brand.icon),
            modifier = Modifier.size(40.dp),
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "•••• •••• •••• ${card.last4!!}",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
