@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.salazar.cheers.feature.map.screens.map.AddPostViewAnnotation
import com.salazar.cheers.feature.map.screens.map.UiLayer
import com.salazar.cheers.feature.map.ui.components.MapComponent

@Composable
fun MapPostHistoryScreen(
    uiState: MapPostHistoryUiState,
    mapViewportState: MapViewportState,
) {
    MapComponent(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        overlay = {
            UiLayer(
                isPublic = false,
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                onMapUIAction = {},
                onZoomTo = {},
                zoom = 15.0,
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
