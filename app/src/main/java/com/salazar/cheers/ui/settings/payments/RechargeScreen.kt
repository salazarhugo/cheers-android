package com.salazar.cheers.ui.settings.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.share.Toolbar

data class Recharge(
    val coins: Int,
    val price: Float
) {

}

@Composable
fun RechargeScreen(
    onRecharge: (Recharge) -> Unit,
    recharges: List<Recharge>,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Recharge") },
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Coins Balance",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "0",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            DividerM3(modifier = Modifier.padding(vertical = 16.dp))
            LazyColumn() {
                items(recharges) {
                    RechargeItem(it, onRecharge = onRecharge)
                }
            }
        }
    }
}

@Composable
fun RechargeItem(
    recharge: Recharge,
    onRecharge: (Recharge) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "${recharge.coins} coins",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = { onRecharge(recharge) },
            shape = RoundedCornerShape(8.dp),
            modifier= Modifier.width(100.dp)
        ) {
            Text(text = "$${recharge.price}")
        }
    }
}
