package com.salazar.cheers.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.main.detail.StaticMap

@Composable
fun EventVenue(
    modifier: Modifier = Modifier,
    address: String,
    latitude: Double,
    longitude: Double,
    onMapClick: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            "About the venue",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        StaticMap(
            latitude = latitude,
            longitude = longitude,
            onMapClick = onMapClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .aspectRatio(2f)
                .clip(MaterialTheme.shapes.medium),
        )
        EventHeaderItem(
            icon = Icons.Default.Directions,
            text = address,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        EventHeaderItem(
            icon = Icons.Default.DirectionsCarFilled,
            text = "Parking: Street",
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

