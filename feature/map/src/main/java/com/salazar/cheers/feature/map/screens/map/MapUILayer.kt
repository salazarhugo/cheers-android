package com.salazar.cheers.feature.map.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.extensions.noRippleClickable

@Composable
fun MapUILayer(
    zoom: Double,
    isPublic: Boolean,
    modifier: Modifier = Modifier,
    onMapUIAction: (MapUIAction) -> Unit,
    onZoomTo: (Double) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        MapTopBar(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            isPublic = isPublic,
            onMapUIAction = onMapUIAction,
        )
        MapSliderComponent(
            zoom = zoom,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            onValueChange = onZoomTo,
        )
        MapBottomBar(
            onMapUIAction = onMapUIAction,
        )
    }
}

@Composable
fun MapTopBar(
    isPublic: Boolean,
    modifier: Modifier = Modifier,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val icon = when (isPublic) {
        true -> Icons.Default.Public
        false -> Icons.Default.PublicOff
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MapButton(
            icon = icon,
            onClick = { onMapUIAction(MapUIAction.OnPublicToggle) },
        )
        MapInfoBarComponent(
            modifier = Modifier.weight(1f),
            text = "Puiseux-en-France",
        )
        MapButton(
            icon = Icons.Default.Settings,
            onClick = { onMapUIAction(MapUIAction.OnSettingsClick) },
        )
    }
}

@Composable
fun MapBottomBar(
    onMapUIAction: (MapUIAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        MapButton(
            icon = Icons.Default.NearMe,
            onClick = { onMapUIAction(MapUIAction.OnMyLocationClick) },
        )
    }
}

@Composable
fun MapButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .noRippleClickable { onClick() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@ScreenPreviews
@Composable
private fun MapUiLayerPreview() {
   CheersPreview {
       MapUILayer(
           zoom = 13.0,
           isPublic = true,
           modifier = Modifier.background(Color.Gray),
           onMapUIAction = {},
           onZoomTo = {},
       )
   }
}
