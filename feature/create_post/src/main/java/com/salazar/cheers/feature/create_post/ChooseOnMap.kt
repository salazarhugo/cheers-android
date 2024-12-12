package com.salazar.cheers.feature.create_post

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.Permission

@Composable
fun ChooseOnMapScreen(
    onBackPressed: () -> Unit,
    onSelectLocation: (Point, Double) -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context = context) }

    Scaffold(
        topBar = {
            ChooseOnMapAppBar(
                onBackPressed = onBackPressed,
                onSelectLocation = onSelectLocation,
                mapView = mapView,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
            }
        }
    ) {
        Permission(Manifest.permission.ACCESS_COARSE_LOCATION) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = it.calculateBottomPadding())
            ) {
                AndroidView(factory = { mapView }, Modifier.fillMaxSize()) {
                    onMapReady(it, context)
                }
                Icon(
                    Icons.Default.Place,
                    "",
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

private fun onMapReady(
    mapView: MapView,
    context: Context
) {
    mapView.gestures.rotateEnabled = false
    mapView.attribution.enabled = false
    mapView.scalebar.enabled = false


    mapView.mapboxMap.setCamera(
        CameraOptions.Builder()
            .zoom(1.0)
            .build()
    )

    val style =
//        if (context.isDarkModeOn())
        "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
//    else
//        "mapbox://styles/salazarbrock/ckzsmluho004114lmeb8rl2zi"

    mapView.mapboxMap.loadStyleUri(style) {
        val positionChangedListener = onIndicatorPositionChangedListener(mapView)
        initLocationComponent(mapView, context, positionChangedListener)
        setupGesturesListener(mapView, positionChangedListener)
    }
}

private fun setupGesturesListener(
    mapView: MapView,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener
) {
    mapView.gestures.addOnMoveListener(onMoveListener(mapView, onIndicatorPositionChangedListener))
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

    override fun onMoveEnd(detector: MoveGestureDetector) {}
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

private fun initLocationComponent(
    mapView: MapView,
    context: Context,
    onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener,
) {
    val locationComponentPlugin = mapView.location
    locationComponentPlugin.updateSettings {
        this.enabled = true
        this.locationPuck = LocationPuck2D(
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

private fun onIndicatorPositionChangedListener(
    mapView: MapView,
) = OnIndicatorPositionChangedListener {
    mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).zoom(13.0).build())
    mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
}

@Composable
fun ChooseOnMapAppBar(
    mapView: MapView,
    onBackPressed: () -> Unit,
    onSelectLocation: (Point, Double) -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Choose post location",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                    fontSize = 14.sp
                )
                Text(
                    text = "Pan and zoom map under pin",
                    fontSize = 14.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        actions = {
            Button(
                onClick = {
                    val center = mapView.mapboxMap.cameraState.center
                    val zoom = mapView.mapboxMap.cameraState.zoom
                    onSelectLocation(center, zoom)
                },
            ) {
                Text("Done")
            }
        })
}
