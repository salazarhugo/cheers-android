@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.screens.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.map.UserLocation
import com.salazar.cheers.feature.map.ui.annotations.CurrentUserAnnotation
import com.salazar.cheers.feature.map.ui.annotations.FriendAnnotation
import com.salazar.cheers.feature.map.ui.annotations.PostAnnotation
import com.salazar.common.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    uiState: MapUiState.Initialized,
    mapViewportState: MapViewportState,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val selectedAnnotation = uiState.selected

    LaunchedEffect(Unit) {
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(Point.fromLngLat(uiState.userLocation.longitude, uiState.userLocation.latitude))
                zoom(13.0)
            }
        )
    }

    Scaffold(
    ) {
        it
        if (selectedAnnotation != null) {
            MapBottomSheet(
                state = uiState.sheetState,
                type = selectedAnnotation,
//                modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
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
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
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
                }
            ) {
                val userLocation = uiState.userLocation
                val isSelected = selectedAnnotation is MapAnnotation.UserAnnotation &&
                            selectedAnnotation.user.id == userLocation.id
                CurrentUserViewAnnotation(
                    isSelected = isSelected,
                    userLocation = userLocation,
                    ghostMode = uiState.ghostMode,
                    onClick = {
                        onMapUIAction(MapUIAction.OnUserViewAnnotationClick(userLocation))
                    },
                )

                uiState.posts?.forEach { post ->
                    AddPostViewAnnotation(
                        post = post,
                        isSelected = false,
                        onClick = {
                            onMapUIAction(MapUIAction.OnPostViewAnnotationClick(post))
                        },
                    )
                }

                uiState.users.forEach { user ->
                    val isSelected = selectedAnnotation is MapAnnotation.UserAnnotation &&
                            selectedAnnotation.user.id == user.id
                    AddFriendViewAnnotation(
                        isSelected = isSelected,
                        userLocation = user,
                        onClick = {
                            onMapUIAction(MapUIAction.OnUserViewAnnotationClick(user))
                        },
                    )
                }
            }
            UiLayer(
                isPublic = uiState.isPublic,
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                onMapUIAction = onMapUIAction,
            )
        }
    }
}


@Composable
fun CurrentUserViewAnnotation(
    userLocation: com.salazar.cheers.data.map.UserLocation,
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
    userLocation: com.salazar.cheers.data.map.UserLocation,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    val point = Point.fromLngLat(userLocation.longitude, userLocation.latitude)

    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(point)
            allowOverlap(true)
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

@Composable
fun AddPostViewAnnotation(
    post: Post,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    val point = Point.fromLngLat(post.longitude, post.latitude)

    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(point)
            allowOverlap(true)
        }
    ) {
        PostAnnotation(
            post = post,
            isSelected = isSelected,
            onClick = onClick,
//                modifier = Modifier.size(120.dp),
//                post = post,
//                onClick = {
//                    onMapUIAction(MapUIAction.OnPostClick(post))
//                },
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
    isPublic: Boolean,
    modifier: Modifier = Modifier,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        MapTopBar(
            isPublic = isPublic,
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
