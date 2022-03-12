package com.salazar.cheers.ui.main.event.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.internal.EventUi
import com.salazar.cheers.ui.theme.Typography
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EventDetailScreen(
    uiState: EventDetailUiState
) {
    Scaffold {
        when (uiState) {
            is EventDetailUiState.HasEvent -> Event(eventUi = uiState.eventUi)
            is EventDetailUiState.NoEvents -> {
                Text("No event")
            }
        }
    }
}

@Composable
fun Event(eventUi: EventUi) {
    Column {
        EventHeader(eventUi = eventUi)
        EventBody(event = eventUi)
    }
}

@Composable
fun EventHeader(eventUi: EventUi) {
    val event = eventUi.event
    Image(
        painter = rememberImagePainter(
            data = event.imageUrl,
            builder = {
                error(com.salazar.cheers.R.drawable.image_placeholder)
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f),
        contentScale = ContentScale.Crop,
    )
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        val d = remember { ZonedDateTime.parse(event.startDate) }
        Text(
            d.toLocalDateTime().format(DateTimeFormatter.ofPattern("E, d MMM hh:mm a")),
            style = MaterialTheme.typography.bodyMedium
        )
        if (event.name.isNotBlank())
            Text(
                event.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        Text("${
            event.type.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } event"
        )
        if (event.description.isNotBlank())
            Text(
                event.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        if (event.locationName.isNotBlank())
            Text(text = event.locationName, style = Typography.labelSmall)
        Text("4.8k interested - 567 going", modifier = Modifier.padding(vertical = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilledTonalButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.StarBorder, null)
                Spacer(Modifier.width(8.dp))
                Text("Interested")
            }
            FilledTonalButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Going")
            }
        }
    }
}

@Composable
fun EventBody(event: EventUi) {
}
