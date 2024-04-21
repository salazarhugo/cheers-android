@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapboxMapComposable
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun MapComponent(
    mapViewportState: MapViewportState,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: (@Composable @MapboxMapComposable MapboxMapScope.() -> Unit)? = null,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        MapboxMap(
            modifier = modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            gesturesSettings = GesturesSettings {
                rotateEnabled = false
            },
            attribution = {},
            scaleBar = {},
            mapInitOptionsFactory = { context ->
                MapInitOptions(
                    context = context,
                    styleUri = "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0",
                    cameraOptions = CameraOptions.Builder()
                        .zoom(1.0)
                        .build()
                )
            },
            content = content,
        )
        overlay()
    }
}

@ScreenPreviews
@Composable
private fun MapComponentPreview() {
    CheersPreview {
        MapComponent(
            mapViewportState = rememberMapViewportState(),
        )
    }
}