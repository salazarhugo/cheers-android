package com.salazar.cheers.components.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.dateTimeFormatter

@Composable
fun EventDetails(
    name: String,
    privacy: Privacy,
    startTimeSeconds: Long,
    onEventDetailsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onEventDetailsClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column() {
            Text(
                text = dateTimeFormatter(timestamp = startTimeSeconds),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${privacy.title} - ${privacy.subtitle}",
                style = MaterialTheme.typography.labelLarge
            )

        }
        Icon(Icons.Outlined.ChevronRight, null)
    }
}

