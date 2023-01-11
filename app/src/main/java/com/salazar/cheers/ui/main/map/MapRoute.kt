package com.salazar.cheers.ui.main.map

import android.annotation.SuppressLint
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.location
import com.salazar.cheers.data.location.DefaultLocationClient
import com.salazar.cheers.navigation.CheersNavigationActions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Stateful composable that displays the Navigation route for the Map screen.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@SuppressLint("MissingPermission")
@Composable
fun MapRoute(
    mapViewModel: MapViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = true,
        )
        onDispose {
            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = true,
            )
        }
    }

    val mapView = remember {
        MapView(
            context = context,
        )
    }
//    val locationClient = DefaultLocationClient(
//        context = context,
//        client = LocationServices.getFusedLocationProviderClient(context),
//    )

    MapScreen(
        uiState = uiState,
        mapView = mapView,
        onMapUIAction = { action ->
            when (action) {
                MapUIAction.OnBackPressed -> navActions.navigateBack()
                MapUIAction.OnCreatePostClick -> navActions.navigateToCreatePost()
                MapUIAction.OnPublicToggle -> mapViewModel.onTogglePublic()
                MapUIAction.OnSwipeRefresh -> TODO()
                MapUIAction.OnSettingsClick -> navActions.navigateToMapSettings()
                is MapUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userID)
                is MapUIAction.OnMapReady -> {
                    mapViewModel.onMapReady(action.map)
                    scope.launch {
                        mapViewModel.mapRepository.onMapReady(action.map, action.ctx)
                    }
                }
                is MapUIAction.OnPostClick -> {
                    scope.launch {
                        uiState.postSheetState.animateTo(ModalBottomSheetValue.HalfExpanded)
                    }
                    mapViewModel.selectPost(action.post)
                }
                MapUIAction.OnMyLocationClick -> {
                    mapViewModel.onMyLocationClick()
                    scope.launch {
                        val location = LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
                        val cameraOptions = CameraOptions.Builder()
                            .center(Point.fromLngLat(location.longitude, location.latitude))
                            .zoom(13.0)
                            .build()
                        mapView.getMapboxMap().flyTo(cameraOptions)
                    }
                }
            }
        },
    )
}