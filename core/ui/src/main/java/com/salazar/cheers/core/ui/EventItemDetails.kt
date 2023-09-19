package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.theme.StrongRed
import com.salazar.cheers.core.util.dateTimeFormatter

@Composable
fun EventItemDetails(
    name: String,
    hostName: String,
    price: Int?,
    startTimeSeconds: Long,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = hostName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = dateTimeFormatter(timestamp = startTimeSeconds),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.error,
                )
                if (price != null)
                    PriceTag(
                        price = price,
                    )
            }
        }
        Icon(Icons.Outlined.Star, null)
    }
}

@Composable
fun PriceTag(
    price: Int,
) {
    val text = if (price == 0)
        "Free"
    else
        "${String.format("%.2f", price / 10E2)}€"

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(StrongRed)
            .padding(horizontal = 8.dp),
        color = Color.White,
    )
}
