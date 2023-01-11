package com.salazar.cheers.ui.main.map

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.salazar.cheers.R
import com.salazar.cheers.ui.compose.utils.Permission
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.compose.extensions.noRippleClickable
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.util.Utils
import com.salazar.cheers.util.Utils.getCircularBitmapWithWhiteBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

@Composable
fun MapScreen(
    uiState: MapUiState,
    mapView: MapView,
    onMapUIAction: (MapUIAction) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.geojson) {
        if (uiState.geojson != null)
            mapView.getMapboxMap().getStyle {
                val users = it.getSourceAs<GeoJsonSource>("users")
                users?.data(uiState.geojson.toJson())
            }
    }

    ModalBottomSheetLayout(
        sheetState = uiState.postSheetState,
        sheetContent = {
            PostMapScreen(
                uiState = uiState,
                onUserClick = { onMapUIAction(MapUIAction.OnUserClick(it)) },
            )
        },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        sheetElevation = 0.dp,
        scrimColor = Color.Transparent
    ) {
        Scaffold(
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
            it
            Box(
                contentAlignment = Alignment.BottomCenter,
            ) {
                Permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = {
                            onMapUIAction(MapUIAction.OnMapReady(mapView, context))
                            mapView
                        },
                    ) { mapView ->
                        scope.launch {
                            uiState.posts?.forEach { post ->
                                launch {
                                    addPostsAnnotation(
                                        post = post,
                                        mapView = mapView,
                                        onSelectPost = { post ->
                                            onMapUIAction(MapUIAction.OnPostClick(post))
                                        }
                                    )
                                }
                            }
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
}

fun addUserAnnotations(
    context: Context,
    features: List<Feature>,
    mapView: MapView,
) {
    val annotationApi = mapView.annotations
    features.forEach {
        val point = it.geometry() as Point
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()
        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmapFromDrawableRes(context, resourceId = R.drawable.ic_bitmoji)!!)
            .withIconAnchor(iconAnchor = IconAnchor.BOTTOM)
        pointAnnotationManager.create(pointAnnotationOptions)
    }
}

suspend fun addPostsAnnotation(
    post: Post,
    mapView: MapView,
    onSelectPost: (Post) -> Unit,
) {
    if (post.photos.isEmpty())
        return

    val annotationApi = mapView.annotations
    val btm = getBitmapFromUrl(post.profilePictureUrl)
    val pointAnnotationManager = annotationApi.createPointAnnotationManager()
    val pointAnnotationOptions = PointAnnotationOptions()
        .withPoint(Point.fromLngLat(post.longitude, post.latitude))
    if (btm != null)
        pointAnnotationOptions.withIconImage(btm)
//                .withIconImage(bitmapFromDrawableRes(context, resourceId = R.drawable.ic_beer)!!)
    pointAnnotationManager.create(pointAnnotationOptions)
    pointAnnotationManager.addClickListener(
        onPostAnnotationClick(
            post,
            onSelectPost = onSelectPost,
            mapView = mapView,
        )
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

private suspend fun getBitmapFromUrl(url: String) = withContext(Dispatchers.IO) {
    val urlObj = URL(url)

    return@withContext try {

        val bitmap = BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())

        ThumbnailUtils.extractThumbnail(bitmap, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            .getCircularBitmapWithWhiteBorder()
    } catch (e: IOException) {
        null
    }
}

private fun onPostAnnotationClick(
    post: Post,
    onSelectPost: (Post) -> Unit,
    mapView: MapView,
): OnPointAnnotationClickListener {
    return OnPointAnnotationClickListener {
        onSelectPost(post)
        mapView.getMapboxMap().flyTo(
            cameraOptions = CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        post.longitude,
                        post.latitude - 0.01
                    )
                )
                .zoom(13.0)
                .build(),
            animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(2000)
            }
        )
        true
    }
}

private fun bitmapFromDrawableRes(
    context: Context,
    @DrawableRes resourceId: Int
) =
    Utils.convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


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
