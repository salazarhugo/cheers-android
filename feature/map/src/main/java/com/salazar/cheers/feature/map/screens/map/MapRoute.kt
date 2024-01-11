@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.screens.map

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.launch
import kotlin.math.max

@SuppressLint("MissingPermission")
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToMapSettings: () -> Unit,
    navigateToCreatePost: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val paris = Point.fromLngLat(2.3522, 48.8566)

    val mapViewportState = rememberMapViewportState {
//        gestures.rotateEnabled = false
//        attribution.enabled = false
//        scalebar.enabled = false
        setCameraOptions {
            center(paris)
            zoom(1.0)
            pitch(0.0)
        }
    }

    MapScreen(
        uiState = uiState,
        mapViewportState = mapViewportState,
        onMapUIAction = { action ->
            when (action) {
                MapUIAction.OnBackPressed -> navigateBack()
                MapUIAction.OnCreatePostClick -> navigateToCreatePost()
                MapUIAction.OnPublicToggle -> viewModel.onTogglePublic()
                MapUIAction.OnSwipeRefresh -> TODO()
                MapUIAction.OnSettingsClick -> navigateToMapSettings()
                is MapUIAction.OnUserClick -> {}//navigateToOtherProfile(action.userID)
                is MapUIAction.OnMapReady -> {
                    viewModel.onMapReady(action.map)
                    scope.launch {
                        viewModel.mapRepository.onMapReady(action.map, action.ctx)
                    }
                }

                MapUIAction.OnMyLocationClick -> {
                    viewModel.onMyLocationClick()

                    val point = Point.fromLngLat(
                        uiState.userLocation?.longitude ?: 0.0,
                        uiState.userLocation?.latitude ?: 0.0,
                    )

                    mapViewportState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(13.0)
                            .build(),
                        animationOptions = MapAnimationOptions.mapAnimationOptions {
                            duration(1000)
                        }
                    )
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

                    scope.launch {
                        uiState.sheetState.expand()
                    }

                    val point = Point.fromLngLat(
                        userLocation.longitude,
                        userLocation.latitude,
                    )

                    val zoom = max(14.0, mapViewportState.cameraState.zoom)

                    mapViewportState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(zoom)
                            .build(),
                        animationOptions = MapAnimationOptions.mapAnimationOptions {
                            duration(1000)
                        }
                    )
                    viewModel.onUserViewAnnotationClick(userLocation = userLocation)
                }

                is MapUIAction.OnChatClick -> {}//navigateToChatWithUserId(action.userID)
                is MapUIAction.OnCommentClick -> {} //navActions.navigateToComments(action.postID)
                MapUIAction.OnDismissBottomSheet -> viewModel.onDismissBottomSheet()
            }
        },
    )
}