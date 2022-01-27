package com.salazar.cheers.ui.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.ui.theme.GreySheet
import com.salazar.cheers.util.Utils
import com.salazar.cheers.util.Utils.getCircledBitmap
import com.snapchat.kit.sdk.Bitmoji
import com.snapchat.kit.sdk.bitmoji.networking.FetchAvatarUrlCallback
import kotlinx.coroutines.*
import java.net.URL

@Composable
fun MapScreen(
    uiState: MapUiState,
    modifier: Modifier = Modifier,
    onCityChanged: (String) -> Unit,
    onSelectPost: (Post) -> Unit,
    navigateToSettingsScreen: () -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    ModalBottomSheetLayout(
        sheetState = uiState.postSheetState,
        sheetContent = { PostMapScreen(uiState = uiState) },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        sheetElevation = 0.dp,
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.MyLocation, "w")
                }
            }
        ) {
            LocationPermission(navigateToSettingsScreen) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    AndroidView(factory = { mapView }, Modifier.fillMaxSize()) {
                        onMapReady(it, context)
                    }
                    UiLayer(this, uiState = uiState, mapView = mapView, onSelectPost = onSelectPost)
                }
            }
        }
    }
}

@Composable
private fun LocationPermission(
    navigateToSettingsScreen: () -> Unit,
    content: @Composable () -> Unit,
) {
    // Track if the user doesn't want to see the rationale any more.
    val doNotShowRationale = rememberSaveable { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            if (doNotShowRationale.value)
                Text("Feature not available")
            else
                LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
        },
        permissionNotAvailableContent = {
            Column {
                Text(
                    "Location permission denied. See this FAQ with information about why we " +
                            "need this permission. Please, grant us access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = navigateToSettingsScreen) {
                    Text("Open Settings")
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun UiLayer(
    scope: BoxScope,
    uiState: MapUiState,
    mapView: MapView,
    onSelectPost: (Post) -> Unit,
) {
    val context = LocalContext.current
    val scope2 = rememberCoroutineScope()
    scope.apply {
        uiState.posts?.forEach {
            if (it.type == PostType.IMAGE)
                addPostToMap(it, postSheetState = uiState.postSheetState, mapView, context, onSelectPost = onSelectPost, scope = scope2)
        }

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = uiState.city,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.5f),
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(bottom = 26.dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .clickable {
//                    findNavController().navigate(R.id.addDialogFragment)
                }
        ) {}
    }
}

private fun initLocationComponent(
    mapView: MapView,
    context: Context,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener,
) {
    val locationComponentPlugin = mapView.location
    locationComponentPlugin.updateSettings {
        this.enabled = true
        this.locationPuck = LocationPuck2D(
            topImage = AppCompatResources.getDrawable(
                context,
                com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon
            ),
            bearingImage = AppCompatResources.getDrawable(
                context,
                com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_bearing_icon
            ),
            scaleExpression = interpolate {
                linear()
                zoom()
                stop {
                    literal(0.0)
                    literal(0.6)
                }
                stop {
                    literal(20.0)
                    literal(1.0)
                }
            }.toJson()
        )
    }
    locationComponentPlugin.addOnIndicatorPositionChangedListener(
        onIndicatorPositionChangedListener
    )
}

private fun addPostToMap(
    post: Post,
    postSheetState: ModalBottomSheetState,
    mapView: MapView,
    context: Context,
    onSelectPost: (Post) -> Unit,
    scope: CoroutineScope,
) {
//    val postPhoto = getBitmapFromUrl(post.photoUrl)
    bitmapFromDrawableRes(
        context,
        R.drawable.ic_cheers_logo
    )?.let {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(post.locationLongitude, post.locationLatitude))
            .withIconImage(it)
        pointAnnotationManager.create(pointAnnotationOptions)
        pointAnnotationManager.addClickListener(
            onPostAnnotationClick(post, postSheetState, scope, onSelectPost = onSelectPost)
        )
    }
}

private fun getBitmapFromUrl(url: String): Bitmap {
    val urlObj = URL(url)
    return BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())
        .getCircledBitmap()
}

private fun onPostAnnotationClick(
    post: Post,
    postSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    onSelectPost: (Post) -> Unit,
): OnPointAnnotationClickListener {
    return OnPointAnnotationClickListener {
        onSelectPost(post)
        scope.launch {
            postSheetState.show()
        }
        true
    }
}

private fun bitmapFromDrawableRes(
    context: Context,
    @DrawableRes resourceId: Int
) =
    Utils.convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


private fun onIndicatorPositionChangedListener(
    mapView: MapView,
) = OnIndicatorPositionChangedListener {
    mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).zoom(13.0).build())
    mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
}

private fun onCameraTrackingDismissed(
    mapView: MapView,
    onMoveListener: OnMoveListener,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener,
) {
    mapView.location
        .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    mapView.gestures.removeOnMoveListener(onMoveListener)
}

private fun onMoveListener(
    mapView: MapView,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener
) = object : OnMoveListener {
    override fun onMoveBegin(detector: MoveGestureDetector) {
        onCameraTrackingDismissed(mapView, this, onIndicatorPositionChangedListener)
    }

    override fun onMove(detector: MoveGestureDetector): Boolean {
        return false
    }

    override fun onMoveEnd(detector: MoveGestureDetector) {
        val cameraState = mapView.getMapboxMap().cameraState
        val center = cameraState.center

        val queryType = when {
            cameraState.zoom < 5.0 -> QueryType.COUNTRY
            cameraState.zoom < 10.0 -> QueryType.REGION
            else -> QueryType.PLACE
        }

        val options = ReverseGeoOptions(
            center = center,
            types = listOf(queryType)
        )
        searchRequestTask = reverseGeocoding.search(options, searchCallback)
    }
}

private fun setupGesturesListener(
    mapView: MapView,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener
) {
    mapView.gestures.addOnMoveListener(onMoveListener(mapView, onIndicatorPositionChangedListener))
}

private fun onCameraTrackingDismissed(
    mapView: MapView,
    onMoveListener: OnMoveListener
) {
    mapView.location
        .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener(mapView))
    mapView.gestures.removeOnMoveListener(onMoveListener)
}

private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
private lateinit var searchRequestTask: SearchRequestTask

private val searchCallback = object : SearchCallback {

    override fun onResults(
        results: List<SearchResult>,
        responseInfo: ResponseInfo
    ) {
        if (results.isEmpty()) {
            Log.i("SearchApiExample", "No reverse geocoding results")
        } else {
            Log.i("SearchApiExample", "Reverse geocoding results: $results")
//            onCityChanged(results.first().name)
//            viewModel.updateCity(results.first().name)
        }
    }

    override fun onError(e: Exception) {
        Log.i("SearchApiExample", "Reverse geocoding error", e)
    }

}

private fun onMapReady(
    mapView: MapView,
    context: Context
) {
    reverseGeocoding = MapboxSearchSdk.getReverseGeocodingSearchEngine()
    mapView.gestures.rotateEnabled = false
    mapView.attribution.enabled = false
    mapView.scalebar.enabled = false

    mapView.getMapboxMap().setCamera(
        CameraOptions.Builder()
            .zoom(1.0)
            .build()
    )

    val style = if (true)
        "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
    else
        "mapbox://styles/salazarbrock/cjx6b2vma1gm71cuwxugjhm1k"

    mapView.getMapboxMap().loadStyleUri(style) {
        val positionChangedListener = onIndicatorPositionChangedListener(mapView)
        initLocationComponent(mapView, context, positionChangedListener)
        setupGesturesListener(mapView, positionChangedListener)
    }
}

private fun getBitmojiAvatar(
    context: Context,
    onSuccess: (String) -> Unit
) {
    Bitmoji.fetchAvatarUrl(context, object : FetchAvatarUrlCallback {
        override fun onSuccess(@Nullable avatarUrl: String?) {
            if (avatarUrl != null)
                onSuccess(avatarUrl)
        }

        override fun onFailure(
            isNetworkError: Boolean,
            statusCode: Int
        ) {
//            toast(statusCode.toString())
        }
    })
}
