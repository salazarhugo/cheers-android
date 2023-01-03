package com.salazar.cheers.ui.main.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.maps.MapView
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the MapPostHistory screen.
 *
 * @param mapPostHistoryViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MapPostHistoryRoute(
    mapPostHistoryViewModel: MapPostHistoryViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by mapPostHistoryViewModel.uiState.collectAsState()

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
