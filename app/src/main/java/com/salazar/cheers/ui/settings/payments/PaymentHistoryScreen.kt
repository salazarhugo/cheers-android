package com.salazar.cheers.ui.settings.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.share.Toolbar
import com.salazar.cheers.internal.Payment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistoryScreen(
    onBackPressed: () -> Unit,
    payments: List<Payment>
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Coin history") },
    ) {
        LazyColumn() {
            items(payments) {
                PaymentHistoryItem(
                    payment = it
                )
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(
    payment: Payment,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = (payment.amount?.div(100)).toString() + " " + payment.currency?.toUpperCase(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = payment.status.capitalize(),
            style = MaterialTheme.typography.bodyMedium
        )
        val dateFormatter = SimpleDateFormat("dd/MM HH:mm")
        val created = Date(payment.created * 1000)
        Text(
            text = dateFormatter.format(created),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
