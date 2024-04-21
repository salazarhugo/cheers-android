@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.map.ui.MapPostHistoryScreen
import com.salazar.cheers.feature.map.ui.MapPostHistoryViewModel

@Composable
fun MapPostHistoryRoute(
    mapPostHistoryViewModel: MapPostHistoryViewModel = hiltViewModel(),
) {
    val uiState by mapPostHistoryViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(1.0)
            pitch(0.0)
        }
    }

    MapPostHistoryScreen(
        uiState = uiState,
        mapViewportState = mapViewportState,
    )
}
