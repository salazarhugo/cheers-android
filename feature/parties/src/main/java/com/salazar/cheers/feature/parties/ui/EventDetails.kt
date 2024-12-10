package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.util.dateTimeFormatter

@Composable
fun PartyDetails(
    name: String,
    privacy: Privacy,
    startTimeSeconds: Long,
    onPartyDetailsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onPartyDetailsClick() }
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = dateTimeFormatter(startTimestamp = startTimeSeconds),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.error,
            )
            if (name.isNotBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Text(
                text = "${privacy.title} - ${privacy.subtitle}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun PartyDetailsPreview() {
    CheersPreview {
        PartyDetails(
            name = "",
            privacy = Privacy.PUBLIC,
            startTimeSeconds = 90,
            onPartyDetailsClick = {},
        )
    }
}
