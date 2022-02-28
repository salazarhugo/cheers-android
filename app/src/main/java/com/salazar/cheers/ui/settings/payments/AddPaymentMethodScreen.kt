package com.salazar.cheers.ui.settings.payments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.stripe.android.view.CardInputWidget

@Composable
fun AddPaymentMethodScreenScreen(
) {
    val context = LocalContext.current

    Column() {
        AndroidView(
            modifier = Modifier.padding(16.dp),
            factory = { CardInputWidget(context) },
        ) {

        }
    }
}