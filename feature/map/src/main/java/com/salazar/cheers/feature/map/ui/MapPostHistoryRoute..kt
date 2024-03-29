package com.salazar.cheers.feature.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.maps.MapView
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.map.ui.MapPostHistoryScreen
import com.salazar.cheers.feature.map.ui.MapPostHistoryViewModel

@Composable
fun MapPostHistoryRoute(
    mapPostHistoryViewModel: MapPostHistoryViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by mapPostHistoryViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val mapView = remember {
        MapView(
            context = context,
        )
    }

    MapPostHistoryScreen(
        uiState = uiState,
        mapView = mapView,
        onMapReady = {
//            mapPostHistoryViewModel.mapRepository.onMapReady(
//                mapView = mapView,
//                context = context
//            )
        }
    )
}
