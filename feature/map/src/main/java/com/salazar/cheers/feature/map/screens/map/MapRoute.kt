@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.screens.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.Permission
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    Permission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        onGranted = {
            viewModel.onPermissionGranted()
        }
    ) {
    }

    when (uiState) {
        is MapUiState.NotInitialized -> {
            LoadingScreen()
        }

        is MapUiState.Initialized -> {
            val currentLocation = Point.fromLngLat(
                uiState.userLocation.longitude,
                uiState.userLocation.latitude,
            )
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    center(currentLocation)
                    zoom(INITIAL_ZOOM)
                    pitch(0.0)
                }
            }

            MapScreen(
                uiState = uiState,
                sheetState = sheetState,
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
                        }

                        MapUIAction.OnMyLocationClick -> {
                            viewModel.onMyLocationClick()

                            mapViewportState.flyTo(
                                cameraOptions = CameraOptions.Builder()
                                    .center(currentLocation)
                                    .zoom(13.0)
                                    .build(),
                                animationOptions = MapAnimationOptions.mapAnimationOptions {
                                    duration(1000)
                                }
                            )
                        }

                        is MapUIAction.OnPostViewAnnotationClick -> {
                            val post = action.post

                            scope.launch {
                                sheetState.expand()
                            }

                            val point = Point.fromLngLat(
                                post.longitude,
                                post.latitude,
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

                            viewModel.onPostViewAnnotationClick(action.post)
                        }

                        is MapUIAction.OnUserViewAnnotationClick -> {
                            val userLocation = action.userLocation

                            scope.launch {
                                sheetState.expand()
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
    }
}