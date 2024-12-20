@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.salazar.cheers.data.map.cheersUserLocation
import com.salazar.cheers.feature.map.screens.map.AddPostViewAnnotation
import com.salazar.cheers.feature.map.screens.map.MapUILayer
import com.salazar.cheers.feature.map.ui.components.MapComponent

@Composable
fun MapPostHistoryScreen(
    uiState: MapPostHistoryUiState,
    mapViewportState: MapViewportState,
) {
    MapComponent(
        isDarkMode = false,
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        overlay = {
            MapUILayer(
                isPublic = false,
                friends = emptyList(),
                showMyLocationButton = true,
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                onMapUIAction = {},
                onZoomTo = {},
                zoom = 15.0,
                city = "",
                userLocation = cheersUserLocation,
            )
        }
    ) {
        uiState.posts?.forEach { post ->
            AddPostViewAnnotation(
                post = post,
                isSelected = false,
                onClick = {},
            )
        }

    }
}
