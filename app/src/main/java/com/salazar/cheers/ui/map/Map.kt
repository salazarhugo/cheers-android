package com.salazar.cheers.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.util.StorageUtil
import com.salazar.cheers.util.Utils.convertDrawableToBitmap
import com.salazar.cheers.util.Utils.getCircledBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


@AndroidEntryPoint
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
        reverseGeocoding = MapboxSearchSdk.getReverseGeocodingSearchEngine()
        mapView.gestures.rotateEnabled = false
        mapView.attribution.enabled = false
        mapView.scalebar.enabled = false

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(1.0)
                .build()
        )

        val style = if (requireContext().isDarkThemeOn())
            "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
        else
            "mapbox://styles/salazarbrock/cjx6b2vma1gm71cuwxugjhm1k"

        mapView.getMapboxMap().loadStyleUri(style) {
            initLocationComponent()
            setupGesturesListener()
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
            Box(contentAlignment = Alignment.BottomCenter) {
                AndroidView(factory = ::MapView, Modifier.fillMaxSize()) {
                    mapView = it
                    enableMyLocation()
                }
                UiLayer(this)
            }
        }
    }

    @Composable
    fun UiLayer(scope: BoxScope) {
        scope.apply {
            val uiState = viewModel.uiState.collectAsState().value

            rememberCoroutineScope().launch {
                uiState.posts?.forEach {
                    if (it.type == PostType.IMAGE)
                        addPostToMap(it)
                }
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
                        findNavController().navigate(R.id.addDialogFragment)
                    }
            ) {}
//            Button(
//                shape = RoundedCornerShape(12.dp),
//                onClick = { /*TODO*/ },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFFFFC00)
//                ),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 12.dp
//                ),
//                modifier = Modifier.padding(bottom = 45.dp)
//            ) {
//                Text("Connect my Snapchat avatar")
//            }
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

    private suspend fun addPostToMap(post: Post) = withContext(Dispatchers.IO) {
            val postPhoto = getBitmapFromUrl(post)

            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(post.locationLongitude, post.locationLatitude))
                .withIconImage(postPhoto)
                .withIconSize(0.1)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.addClickListener(onPostAnnotationClick(post))
        }

    private fun getBitmapFromUrl(post: Post): Bitmap {
        val urlObj = URL(post.photoUrl)
        return BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())
            .getCircledBitmap()
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

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchCallback {

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No reverse geocoding results")
            } else {
                Log.i("SearchApiExample", "Reverse geocoding results: $results")
                viewModel.updateCity(results.first().name)
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Reverse geocoding error", e)
        }

    }
}
