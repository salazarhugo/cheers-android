package com.salazar.cheers.ui.main.map

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.salazar.cheers.components.utils.Permission
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.PostType
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
    onCityChanged: (String) -> Unit,
    onSelectPost: (PostFeed) -> Unit,
    onTogglePublic: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    onAddPostClicked: () -> Unit,
    onMapReady: (MapView, Context) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(
            context = context,
        )
    }

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
                onUserClick = onUserClick,
            )
        },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        sheetElevation = 0.dp,
        scrimColor = Color.Transparent
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.MyLocation, null)
                }
            }
        ) {
            Permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = {
                            onMapReady(mapView, context)
                            mapView
                        },
                    ) { mapView ->
//                        addUserAnnotations(
//                            context = context,
//                            features = uiState.users,
//                            mapView = mapView,
//                        )
                        addPostsAnnotations(
                            context = context,
                            posts = uiState.posts,
                            mapView = mapView,
                            onSelectPost = onSelectPost,
                        )
                    }
                    UiLayer(
                        this,
                        uiState = uiState,
                        mapView = mapView,
                        onSelectPost = onSelectPost,
                        onTogglePublic = onTogglePublic,
                        isPublic = uiState.isPublic,
                        onAddPostClicked = onAddPostClicked,
                    )
                }
            }
        }
    }
}

private fun addUserAnnotations(
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

private fun addPostsAnnotations(
    context: Context,
    posts: List<PostFeed>?,
    mapView: MapView,
    onSelectPost: (PostFeed) -> Unit,
) {
    val annotationApi = mapView.annotations
    posts?.forEach {
        if (it.post.type == PostType.IMAGE) {
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(it.post.locationLongitude, it.post.locationLatitude))
                .withIconImage(bitmapFromDrawableRes(context, resourceId = R.drawable.ic_beer)!!)
                .withIconSize(1.5)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.addClickListener(
                onPostAnnotationClick(
                    it,
                    onSelectPost = onSelectPost,
                    mapView = mapView,
                )
            )
        }
    }
}

@Composable
fun UiLayer(
    scope: BoxScope,
    uiState: MapUiState,
    mapView: MapView,
    isPublic: Boolean,
    onSelectPost: (PostFeed) -> Unit,
    onTogglePublic: () -> Unit,
    onAddPostClicked: () -> Unit,
) {
    val context = LocalContext.current
    val scope2 = rememberCoroutineScope()

    scope.apply {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
                .clickable { scope2.launch { uiState.postSheetState.hide() } },
        ) {
            Text(
                text = uiState.city,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        if (uiState.postSheetState.isVisible)
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clickable { scope2.launch { uiState.postSheetState.hide() } },
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
//        Surface(
//            shape = RoundedCornerShape(22.dp),
//            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(8.dp),
//        ) {
//            Text(
//                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//                text = uiState.city,
//                style = MaterialTheme.typography.titleMedium,
//            )
//        }
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clickable { onTogglePublic() },
        ) {
            val icon = if (isPublic) Icons.Default.Public else Icons.Default.PublicOff
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.5f),
            shadowElevation = 0.dp,
            modifier = Modifier
                .padding(bottom = 26.dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onAddPostClicked() }
        ) {}
    }
}

private suspend fun getBitmapFromUrl(url: String) = withContext(Dispatchers.IO) {
    val urlObj = URL(url)

    return@withContext try {
        BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())
            .getCircularBitmapWithWhiteBorder(180)
    } catch (e: IOException) {
        null
    }
}

private fun onPostAnnotationClick(
    post: PostFeed,
    onSelectPost: (PostFeed) -> Unit,
    mapView: MapView,
): OnPointAnnotationClickListener {
    return OnPointAnnotationClickListener {
        onSelectPost(post)
        mapView.getMapboxMap().flyTo(
            cameraOptions = CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        post.post.locationLongitude,
                        post.post.locationLatitude - 0.01
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


