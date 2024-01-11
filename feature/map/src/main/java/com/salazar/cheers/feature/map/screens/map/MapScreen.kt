@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.screens.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.salazar.cheers.core.ui.ui.Permission
import com.salazar.cheers.feature.map.domain.models.UserLocation
import com.salazar.cheers.feature.map.ui.dialogs.PostMapDialog
import com.salazar.cheers.feature.map.ui.annotations.FriendAnnotation
import com.salazar.cheers.feature.map.ui.annotations.CurrentUserAnnotation
import com.salazar.cheers.feature.map.ui.dialogs.UserMapDialog
import com.salazar.common.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    mapViewportState: MapViewportState,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val selectedAnnotation = uiState.selected

    Scaffold {
        it
        if (selectedAnnotation != null) {
            MapBottomSheet(
                state = uiState.sheetState,
                type = selectedAnnotation,
                userLocation = uiState.selectedUser,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
                onMapUIAction = onMapUIAction,
                onDismissRequest = {
                    scope.launch {
                        uiState.sheetState.hide()
                    }.invokeOnCompletion {
                        onMapUIAction(MapUIAction.OnDismissBottomSheet)
                    }
                }
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
        ) {
            Permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    gesturesSettings = GesturesSettings {
                        rotateEnabled = false
                    },
                    attributionSettings = AttributionSettings {
                        enabled = false
                    },
                    scaleBarSettings = ScaleBarSettings {
                        enabled = false
                    },
                    mapInitOptionsFactory = { context ->
                        MapInitOptions(
                            context = context,
                            styleUri = "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0",
                            cameraOptions = CameraOptions.Builder()
                                .zoom(1.0)
                                .build()
                        )
                    }
                ) {
                    val userLocation = uiState.userLocation
                    if (userLocation != null) {
                        CurrentUserViewAnnotation(
                            isSelected = uiState.selectedUser?.id == userLocation.id,
                            userLocation = userLocation,
                            ghostMode = uiState.ghostMode,
                            onClick = {
                                onMapUIAction(MapUIAction.OnUserViewAnnotationClick(userLocation))
                            },
                        )
                    }

                    uiState.users.forEach { user ->
                        AddFriendViewAnnotation(
                            isSelected = uiState.selectedUser?.id == user.id,
                            userLocation = user,
                            onClick = {
                                onMapUIAction(MapUIAction.OnUserViewAnnotationClick(user))
                            },
                        )
                    }
                }
                UiLayer(
                    uiState = uiState,
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxSize()
                        .align(Alignment.TopCenter),
                    onMapUIAction = onMapUIAction,
                )
            }
        }
    }
}


@Composable
fun CurrentUserViewAnnotation(
    userLocation: UserLocation,
    isSelected: Boolean,
    ghostMode: Boolean,
    onClick: () -> Unit,
) {
    val point = Point.fromLngLat(userLocation.longitude, userLocation.latitude)
    val options = viewAnnotationOptions {
        geometry(point)
        selected(true)
    }

    ViewAnnotation(
        options = options
    ) {
        CurrentUserAnnotation(
            name = "Me",
            picture = userLocation.picture,
            ghostMode = ghostMode,
            isSelected = isSelected,
            onClick = onClick,
        )
    }
}

@Composable
fun AddFriendViewAnnotation(
    userLocation: UserLocation,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    val point = Point.fromLngLat(userLocation.longitude, userLocation.latitude)

    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(point)
            allowOverlap(false)
        }
    ) {
        FriendAnnotation(
            isSelected = isSelected,
            name = userLocation.name,
            picture = userLocation.picture,
            onClick = onClick,
        )
    }
}

//suspend fun addPostsAnnotation(
//    post: Post,
//    onMapUIAction: (MapUIAction) -> Unit,
//) {
//    val point = Point.fromLngLat(post.longitude, post.latitude)
//
//        setContent {
//            PostAnnotation(
//                modifier = Modifier.size(120.dp),
//                post = post,
//                onClick = {
//                    onMapUIAction(MapUIAction.OnPostClick(post))
//                },
//            )
//        }
//    mapView.viewAnnotationManager.addViewAnnotation(
//        view = view,
//        options = viewAnnotationOptions {
//            geometry(point)
//            height(120)
//            width(120)
//        }
//    )
//}

@Composable
fun UiLayer(
    uiState: MapUiState,
    modifier: Modifier = Modifier,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        MapTopBar(
            isPublic = uiState.isPublic,
            onMapUIAction = onMapUIAction,
        )
        MapBottomBar(
            onMapUIAction = onMapUIAction,
        )
    }
}

@Composable
fun MapTopBar(
    isPublic: Boolean,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val icon = when (isPublic)  {
        true -> Icons.Default.Public
        false -> Icons.Default.PublicOff
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MapButton(
            icon = icon,
            onClick = { onMapUIAction(MapUIAction.OnPublicToggle) },
        )
        MapButton(
            icon = Icons.Default.Settings,
            onClick = { onMapUIAction(MapUIAction.OnSettingsClick) },
        )
    }
}

@Composable
fun MapBottomBar(
    onMapUIAction: (MapUIAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        MapButton(
            icon = Icons.Default.NearMe,
            onClick = { onMapUIAction(MapUIAction.OnMyLocationClick) },
        )
    }
}

@Composable
fun MapButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .padding(8.dp)
            .noRippleClickable { onClick() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
