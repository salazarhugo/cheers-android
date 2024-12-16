package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.StaticMap
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PartyVenue(
    address: String,
    latitude: Double,
    longitude: Double,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        PartySectionTitle(
            text = "About the venue",
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
        PartyHeaderItem(
            icon = Icons.Default.Directions,
            text = address,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun PartyVenuePreview() {
    CheersPreview {
        PartyVenue(
            modifier = Modifier.padding(16.dp),
            address = "",
            latitude = 0.0,
            longitude = 0.0,
            onMapClick = {},
        )
    }
}
