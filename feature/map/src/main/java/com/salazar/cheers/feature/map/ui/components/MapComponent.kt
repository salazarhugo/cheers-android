package com.salazar.cheers.feature.map.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapboxMapComposable
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun MapComponent(
    isDarkMode: Boolean,
    mapViewportState: MapViewportState,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: (@Composable @MapboxMapComposable MapboxMapScope.() -> Unit)? = null,
) {
    val mapState = rememberMapState {
        gesturesSettings = GesturesSettings { rotateEnabled = false }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        MapboxMap(
            mapState = mapState,
            modifier = modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            attribution = {},
            scaleBar = {},
            style = {
                val cheersDay = "mapbox://styles/salazarbrock/ckzsmluho004114lmeb8rl2zi"
                val cheersNight = "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
                val style = when (isDarkMode) {
                    true -> cheersNight
                    false -> cheersDay
                }
                MapStyle(style = style)
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
            isDarkMode = false,
            mapViewportState = rememberMapViewportState(),
        )
    }
}