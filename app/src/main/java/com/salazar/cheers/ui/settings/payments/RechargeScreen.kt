package com.salazar.cheers.ui.settings.payments

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.billingclient.api.SkuDetails
import com.salazar.cheers.compose.DividerM3
import com.salazar.cheers.compose.animations.AnimatedTextCounter
import com.salazar.cheers.compose.share.Toolbar

@Composable
fun RechargeScreen(
    coins: Int,
    onRecharge: (SkuDetails) -> Unit,
    recharges: List<SkuDetails>,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Recharge") },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Coins Balance",
                    style = MaterialTheme.typography.titleLarge,
                )
                AnimatedTextCounter(
                    targetState = coins,
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            DividerM3(modifier = Modifier.padding(vertical = 16.dp))
            LazyColumn {
                items(recharges) {
                    RechargeItem(it, onRecharge = onRecharge)
                }
            }
        }
    }
}

@Composable
fun RechargeItem(
    recharge: SkuDetails,
    onRecharge: (SkuDetails) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(model = recharge.iconUrl),
                contentDescription = null,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = recharge.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = { onRecharge(recharge) },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.width(100.dp)
        ) {
            Text(text = recharge.price)
        }
    }
}
