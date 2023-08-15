package com.salazar.cheers.feature.map.screens.map

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.max

/**
 * Stateful composable that displays the Navigation route for the Map screen.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@SuppressLint("MissingPermission")
@Composable
fun MapRoute(
    mapViewModel: MapViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToMapSettings: () -> Unit,
    navigateToCreatePost: () -> Unit,
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val paris = Point.fromLngLat(2.3522, 48.8566)
    val mapView = remember {
        MapView(
            context = context,
            mapInitOptions = MapInitOptions(
                context,
                cameraOptions = cameraOptions {
                    center(paris)
                }
            )
        )
    }

    MapScreen(
        uiState = uiState,
        mapView = mapView,
        onMapUIAction = { action ->
            when (action) {
                MapUIAction.OnBackPressed -> navigateBack()
                MapUIAction.OnCreatePostClick -> navigateToCreatePost()
                MapUIAction.OnPublicToggle -> mapViewModel.onTogglePublic()
                MapUIAction.OnSwipeRefresh -> TODO()
                MapUIAction.OnSettingsClick -> navigateToMapSettings()
                is MapUIAction.OnUserClick -> {}//navigateToOtherProfile(action.userID)
                is MapUIAction.OnMapReady -> {
                    mapViewModel.onMapReady(action.map)
                    scope.launch {
                        mapViewModel.mapRepository.onMapReady(action.map, action.ctx)
                    }
                }

                MapUIAction.OnMyLocationClick -> {
                    mapViewModel.onMyLocationClick()
                    scope.launch {
//                        val location =
//                            LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
//                                ?: return@launch
//                        val mapbox = mapView.getMapboxMap()
//                        val cameraOptions = CameraOptions.Builder()
//                            .center(Point.fromLngLat(location.longitude, location.latitude))
//                            .zoom(13.0)
//                            .build()
//                        mapbox.flyTo(cameraOptions)
                    }
                }
//                is MapUIAction.OnPostClick -> {
//                    val post = action.post
//                    val mapbox = mapView.getMapboxMap()
//
//                    scope.launch {
//                        uiState.sheetState.expand()
//                    }
//
//                    val point = Point.fromLngLat(
//                        post.longitude,
//                        post.latitude - 0.006
//                    )
//
//                    val zoom = max(14.0, mapbox.cameraState.zoom)
//
//                    mapbox.flyTo(
//                        cameraOptions = CameraOptions.Builder()
//                            .center(point)
//                            .zoom(zoom)
//                            .build(),
//                        animationOptions = MapAnimationOptions.mapAnimationOptions {
//                            duration(1000)
//                        }
//                    )
//
//                    mapViewModel.selectPost(post = post)
//                }
                is MapUIAction.OnUserViewAnnotationClick -> {
                    val userLocation = action.userLocation
                    val mapbox = mapView.getMapboxMap()

                    scope.launch {
                        uiState.sheetState.expand()
                    }

                    val point = Point.fromLngLat(
                        userLocation.longitude,
                        userLocation.latitude,
                    )

                    val zoom = max(14.0, mapbox.cameraState.zoom)

                    mapbox.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(zoom)
                            .build(),
                        animationOptions = MapAnimationOptions.mapAnimationOptions {
                            duration(1000)
                        }
                    )
                    mapViewModel.onUserViewAnnotationClick(userLocation = userLocation)
                }

                is MapUIAction.OnChatClick -> {}//navigateToChatWithUserId(action.userID)
                is MapUIAction.OnCommentClick -> {} //navActions.navigateToComments(action.postID)
            }
        },
    )
}