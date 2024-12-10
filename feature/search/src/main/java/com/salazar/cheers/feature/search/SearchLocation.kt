package com.salazar.cheers.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ResultItem(
    name: String?,
    icon: String?,
    address: String?,
    onLocationClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onLocationClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = icon,
            contentDescription = null,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            if (name != null)
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                )
            if (address != null)
                Text(
                    text = address,
                    style = MaterialTheme.typography.labelMedium,
                )
        }
    }
}