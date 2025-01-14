@file:OptIn(MapboxExperimental::class)

package com.salazar.cheers.feature.map.screens.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.salazar.cheers.Theme
import com.salazar.cheers.core.Post
import com.salazar.cheers.feature.map.ui.annotations.CurrentUserAnnotation
import com.salazar.cheers.feature.map.ui.annotations.FriendAnnotation
import com.salazar.cheers.feature.map.ui.annotations.PostAnnotation
import com.salazar.cheers.feature.map.ui.components.MapComponent
import kotlinx.coroutines.launch

const val INITIAL_ZOOM = 13.0

@Composable
fun MapScreen(
    sheetState: SheetState,
    isMapCenteredOnMe: Boolean,
    uiState: MapUiState.Initialized,
    mapViewportState: MapViewportState,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val selectedAnnotation = uiState.selected
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState,
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (selectedAnnotation != null) {
                MapBottomSheet(
                    type = selectedAnnotation,
                    onMapUIAction = onMapUIAction,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onMapUIAction(MapUIAction.OnDismissBottomSheet)
                        }
                    }
                )
            }
        },
    ) {
        MapComponent(
            isDarkMode = isDarkTheme(uiState.theme, isSystemInDarkTheme = isSystemInDarkTheme()),
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            overlay = {
                MapUILayer(
                    userLocation = uiState.userLocation,
                    city = uiState.city,
                    friends = uiState.users.filter { it.id != uiState.userLocation.id },
                    showMyLocationButton = !isMapCenteredOnMe,
                    zoom = mapViewportState.cameraState?.zoom ?: INITIAL_ZOOM,
                    isPublic = uiState.isPublic,
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxSize()
                        .align(Alignment.Center),
                    onMapUIAction = onMapUIAction,
                    onZoomTo = {
                        mapViewportState.easeTo(
                            cameraOptions = cameraOptions {
                                zoom(it)
                            },
                            animationOptions = MapAnimationOptions.mapAnimationOptions {
                                duration(0)
                            }
                        )
                    }
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
        options = options,
    ) {
        CurrentUserAnnotation(
            name = userLocation.name,
            username = userLocation.username,
            picture = userLocation.picture,
            ghostMode = ghostMode,
            isSelected = isSelected,
            onClick = onClick,
            lastUpdated = userLocation.lastUpdated,
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
            username = userLocation.username,
            name = userLocation.name,
            picture = userLocation.picture,
            lastUpdated = userLocation.lastUpdated,
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

fun isDarkTheme(
    theme: Theme?,
    isSystemInDarkTheme: Boolean
): Boolean {
    return when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme
        Theme.UNRECOGNIZED -> isSystemInDarkTheme
        null -> true
    }
}

