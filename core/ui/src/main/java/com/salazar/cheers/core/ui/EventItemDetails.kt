package com.salazar.cheers.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.theme.StrongRed
import com.salazar.cheers.core.util.dateTimeFormatter

@Composable
fun EventItemDetails(
    name: String,
    hostName: String,
    price: Int?,
    startTimeSeconds: Long,
    endTimeSeconds: Long,
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
            if (hostName.isNotBlank()) {
                Text(
                    text = hostName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = dateTimeFormatter(
                        startTimestamp = startTimeSeconds,
                        endTimestamp = endTimeSeconds,
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.error,
                )
                if (price != null) {
                    PriceTag(
                        price = price,
                    )
                }
            }
        }
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

@Preview
@Composable
private fun EventItemDetailsPreview() {
    CheersPreview {
        EventItemDetails(
            name = "Nowadays 10 Years Party @ Cabaret Sauvage W/ Fakear & More",
            hostName = "Cabaret Sauvage",
            price = 3255,
            startTimeSeconds = 0L,
            endTimeSeconds = 0L,
        )
    }
}