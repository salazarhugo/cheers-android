package com.salazar.cheers.map.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.salazar.cheers.ui.compose.utils.Permission
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.map.domain.models.UserLocation
import com.salazar.cheers.map.ui.annotations.CurrentUserAnnotation
import com.salazar.cheers.map.ui.annotations.FriendAnnotation
import com.salazar.cheers.map.ui.annotations.PostAnnotation
import com.salazar.cheers.map.ui.dialogs.BottomSheetM3
import com.salazar.cheers.map.ui.dialogs.PostMapDialog
import com.salazar.cheers.map.ui.dialogs.UserMapDialog
import com.salazar.cheers.ui.compose.extensions.noRippleClickable
import com.salazar.cheers.ui.main.home.HomeUIAction
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.core.data.util.Utils
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    uiState: MapUiState,
    mapView: MapView,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = uiState.sheetState)

    BottomSheetScaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(bottomEnd = 22.dp, bottomStart = 22.dp)),
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetContent =  {
            BottomSheetM3 {
                when(uiState.selected) {
                    MapAnnotationType.POST -> PostMapDialog(
                        uiState = uiState,
                        onHomeUIAction = {
                            if (it is HomeUIAction.OnCommentClick)
                                onMapUIAction(MapUIAction.OnCommentClick(it.postID))
                            if (it is HomeUIAction.OnUserClick)
                                onMapUIAction(MapUIAction.OnUserClick(it.userID))
                        }
                    )
                    MapAnnotationType.USER -> UserMapDialog(
                            userLocation = uiState.selectedUser,
                            onClose = {
                                scope.launch {
                                    uiState.sheetState.collapse()
                                }
                            },
                            onChatClick = {
                                onMapUIAction(MapUIAction.OnChatClick(it))
                            }
                        )
                    null -> {}
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        sheetElevation = 0.dp,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onMapUIAction(MapUIAction.OnCreatePostClick) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
        ) {
            Permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        onMapUIAction(MapUIAction.OnMapReady(mapView, it))
                        mapView
                    },
                    update = { mapView ->
                        mapView.viewAnnotationManager.removeAllViewAnnotations()
                        scope.launch {
//                            val userLocation = uiState.userLocation
//                            if (userLocation != null)
//                                launch {
//                                    addCurrentUserViewAnnotation(
//                                        isSelected = false,
//                                        view = currentUserView,
//                                        userLocation = userLocation,
//                                        mapView = mapView,
//                                        onMapUIAction = onMapUIAction,
//                                        ghostMode = ghostMode,
//                                    )
//                                }
                            uiState.users.forEach { user ->
                                launch {
                                    addFriendViewAnnotation(
                                        isSelected = uiState.selectedUser?.id == user.id,
                                        context = context,
                                        userLocation = user,
                                        mapView = mapView,
                                        onMapUIAction = onMapUIAction,
                                    )
                                }
                            }
                            uiState.posts?.forEach { post ->
                                launch {
                                    addPostsAnnotation(
                                        context = context,
                                        post = post,
                                        mapView = mapView,
                                        onMapUIAction = onMapUIAction,
                                    )
                                }
                            }
                        }
                    }
                )
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

@SuppressLint("ResourceType")
fun addCurrentUserViewAnnotation(
    view: View,
    userLocation: UserLocation,
    mapView: MapView,
    isSelected: Boolean,
    ghostMode: Boolean,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val point = Point.fromLngLat(userLocation.longitude, userLocation.latitude)

    val size = 100
    val options = viewAnnotationOptions {
        geometry(point)
        height(size)
        width(size)
    }

    val manager = mapView.viewAnnotationManager

    if (ghostMode) {
        manager.removeViewAnnotation(view)
        return
    }

    if (manager.getViewAnnotationOptionsByView(view) != null) {
        manager.updateViewAnnotation(
            view = view,
            options = options,
        )
    } else {
        manager.addViewAnnotation(
            view = view,
            options = options,
        )
    }
}

fun addFriendViewAnnotation(
    context: Context,
    userLocation: UserLocation,
    mapView: MapView,
    isSelected: Boolean,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val point = Point.fromLngLat(userLocation.longitude, userLocation.latitude)

    val view = ComposeView(context).apply {
        setContent {
            FriendAnnotation(
                isSelected = isSelected,
                name = userLocation.name,
                picture = userLocation.picture,
                onClick = {
                    onMapUIAction(MapUIAction.OnUserViewAnnotationClick(userLocation))
                },
            )
        }
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

   mapView.viewAnnotationManager.removeViewAnnotation(view)

    val size = when(isSelected) {
        true -> 100
        false -> 100
    }

    mapView.viewAnnotationManager.addViewAnnotation(
        view = view,
        options = viewAnnotationOptions {
            geometry(point)
            height(size)
            width(size)
        },
    )
}

suspend fun addPostsAnnotation(
    context: Context,
    post: Post,
    mapView: MapView,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val point = Point.fromLngLat(post.longitude, post.latitude)

    val view = ComposeView(context).apply {
        setContent {
            PostAnnotation(
                modifier = Modifier.size(120.dp),
                post = post,
                onClick = {
                    onMapUIAction(MapUIAction.OnPostClick(post))
                },
            )
        }
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    mapView.viewAnnotationManager.addViewAnnotation(
        view = view,
        options = viewAnnotationOptions {
            geometry(point)
            height(120)
            width(120)
        }
    )
}

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
