package com.salazar.cheers.feature.map.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.salazar.cheers.data.map.UserLocation
import com.salazar.cheers.data.map.cheersUserLocation
import com.salazar.cheers.data.map.cheersUserLocationList

@Composable
fun MapUILayer(
    city: String,
    zoom: Double,
    isPublic: Boolean,
    userLocation: UserLocation,
    friends: List<UserLocation>,
    showMyLocationButton: Boolean,
    modifier: Modifier = Modifier,
    onMapUIAction: (MapUIAction) -> Unit,
    onZoomTo: (Double) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        MapTopBar(
            city = city,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            isPublic = isPublic,
            onMapUIAction = onMapUIAction,
        )
        MapSliderComponent(
            zoom = zoom,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.End)
                .padding(end = 8.dp),
            onValueChange = onZoomTo,
        )
        MapBottomBar(
            friends = friends,
            userLocation = userLocation,
            showMyLocationButton = showMyLocationButton,
            onMapUIAction = onMapUIAction,
        )
    }
}

@Composable
fun MapTopBar(
    city: String,
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
            text = city,
            modifier = Modifier.weight(1f),
        )
        MapButton(
            icon = Icons.Default.Settings,
            onClick = { onMapUIAction(MapUIAction.OnSettingsClick) },
        )
    }
}

@Composable
fun MapBottomBar(
    userLocation: UserLocation,
    friends: List<UserLocation>,
    showMyLocationButton: Boolean,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(
                visible = showMyLocationButton,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                MapButton(
                    icon = Icons.Default.NearMe,
                    onClick = { onMapUIAction(MapUIAction.OnMyLocationClick) },
                )
            }
        }
        AnimatedVisibility(
            visible = friends.isNotEmpty(),
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
            ),
            exit = slideOutVertically(
                targetOffsetY = { it / 2 },
            ),
        ) {
            MapFriendListComponent(
                userLocation = userLocation,
                friends = friends,
                modifier = Modifier.fillMaxWidth(),
                onUserClick = {
                    onMapUIAction(MapUIAction.OnUserViewAnnotationClick(it))
                }
            )
        }
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
            showMyLocationButton = true,
            friends = cheersUserLocationList,
            city = "Dubai",
            userLocation = cheersUserLocation,
        )
    }
}
