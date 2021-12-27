package com.salazar.cheers.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.StorageUtil
import com.salazar.cheers.util.Utils.convertDrawableToBitmap
import com.salazar.cheers.util.Utils.getCircledBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class MapFragment : Fragment() {

    private val viewModel: MapViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MapScreen()
            }
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        )
            onMapReady()
        else
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onMapReady()
            }
        }

    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    private fun onMapReady() {
        mapView.gestures.rotateEnabled = false
        mapView.attribution.enabled = false
        mapView.scalebar.enabled = false
//        mapView.getMapboxMap().addCo

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(1.0)
                .build()
        )

        val style = if (requireContext().isDarkThemeOn())
            "mapbox://styles/mapbox/dark-v10"
        else
            "mapbox://styles/salazarbrock/cjx6b2vma1gm71cuwxugjhm1k"

        mapView.getMapboxMap().loadStyleUri(style) {
            initLocationComponent()
            setupGesturesListener()
            val uiState = viewModel.uiState.value
            lifecycleScope.launch {
                uiState.posts.forEach {
                    addPostToMap(it)
                }
            }
        }
    }
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Scrollbar() {
        // actual composable state
        val zoom by remember { mutableStateOf(0f) }
        Box(
            Modifier
                .fillMaxHeight()
                .width(45.dp)
                .pointerInteropFilter { motionEvent ->
                    when(motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            false
                        }
                        MotionEvent.ACTION_MOVE -> {
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            false
                        }
                        else -> false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(zoom.toString())
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MapScreen() {

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    initLocationComponent()
                    setupGesturesListener()
                }) {
                    Icon(Icons.Default.MyLocation, "w")
                }
            }
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                AndroidView(factory = ::MapView, Modifier.fillMaxSize()) {
                    mapView = it
                    enableMyLocation()
                }
//                var sliderPosition by remember { mutableStateOf(0f) }
//                Slider(
//                    modifier = Modifier
//                        .height(100.dp)
//                        .width(1.dp),
//                    value = sliderPosition,
//                    onValueChange = { sliderPosition = it },
//                    steps = 6,
//                    colors = SliderDefaults.colors(
//                        thumbColor = MaterialTheme.colorScheme.secondary,
//                        activeTrackColor = MaterialTheme.colorScheme.secondary
//                    ),
//                )
                Scrollbar()
            }
        }
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_icon_shadow,
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

    private suspend fun addPostToMap(post: Post) =
        withContext(Dispatchers.IO) {
            getBitmapFromUrl(post) { postPhoto ->
                if (postPhoto == null)
                    return@getBitmapFromUrl

                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(post.locationLongitude, post.locationLatitude))
                    .withIconImage(postPhoto)
                    .withIconSize(0.1)
                pointAnnotationManager.create(pointAnnotationOptions)
                pointAnnotationManager.addClickListener(onPostAnnotationClick(post))
            }
        }

    private fun getBitmapFromUrl(post: Post, onDone: (Bitmap?) -> Unit) {
        StorageUtil.pathToReference(post.photoPath)?.downloadUrl?.addOnSuccessListener {
            val urlObj = URL(it.toString())
            onDone(
                BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())
                    .getCircledBitmap()
            )
        }
    }

    private fun onPostAnnotationClick(post: Post): OnPointAnnotationClickListener {
        return OnPointAnnotationClickListener {
            viewModel.selectPost(post)
            findNavController().navigate(R.id.postMapDialog)
            true
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(it)
                .zoom(13.0)
                .build()
        )
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}
