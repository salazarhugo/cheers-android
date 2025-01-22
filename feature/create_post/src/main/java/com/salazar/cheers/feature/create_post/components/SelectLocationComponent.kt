package com.salazar.cheers.feature.create_post.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.row.AddLocationItem


@Composable
internal fun SelectLocationComponent(
    location: String?,
    locationResults: List<String>,
    modifier: Modifier = Modifier,
    onMapClick: () -> Unit = {},
    onLocationClick: (String) -> Unit = {},
    onDeleteLocation: () -> Unit = {},
) {
    if (location.isNullOrBlank()) {
        AddLocationItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onMapClick,
        )
        LocationResultsComponent(
            modifier = Modifier,
            results = locationResults,
            onLocationClick = onLocationClick,
        )
    } else {
        LocationItem(
            location = location,
            modifier = Modifier.padding(horizontal = 16.dp),
            onDeleteLocation = onDeleteLocation,
        )
    }
}

@Composable
private fun LocationItem(
    location: String,
    modifier: Modifier = Modifier,
    onDeleteLocation: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = location,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        )
        IconButton(
            onClick = onDeleteLocation,
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun SelectLocationComponentPreview_Location() {
    CheersPreview {
        SelectLocationComponent(
            location = "Jameson Distillery Bow St.",
            locationResults = emptyList(),
        )
    }
}

@ComponentPreviews
@Composable
private fun SelectLocationComponentPreview() {
    CheersPreview {
        SelectLocationComponent(
            location = null,
            locationResults = listOf("Dublin", "Ireland"),
        )
    }
}
