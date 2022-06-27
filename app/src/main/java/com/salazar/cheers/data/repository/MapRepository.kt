package com.salazar.cheers.data.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.R
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.location2
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.util.Utils.isDarkModeOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MapRepository @Inject constructor(
) {
    private lateinit var reverseGeocoding: SearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    val searchCallback = object : SearchCallback {
        override fun onResults(
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No reverse geocoding results")
            } else {
//                updateCity(results[0].name)
                Log.i("SearchApiExample", "Reverse geocoding results: $results")
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Reverse geocoding error", e)
        }
    }

    fun onMapReady(
        mapView: MapView,
        context: Context
    ) {
        reverseGeocoding = MapboxSearchSdk.getSearchEngine()
        mapView.gestures.rotateEnabled = false
        mapView.attribution.enabled = false
        mapView.scalebar.enabled = false

        mapView.getMapboxMap().flyTo(
            CameraOptions.Builder()
                .zoom(1.0)
                .build()
        )

        val style = if (context.isDarkModeOn())
            "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
        else
            "mapbox://styles/salazarbrock/ckzsmluho004114lmeb8rl2zi"

        mapView.getMapboxMap().loadStyle(
            com.mapbox.maps.extension.style.style(styleUri = style) {
//                val geojson = uiState.value.geojson
//                +geoJsonSource(id = "users") {
//                    data(geojson?.toJson().toString())
//                    cluster(true)
//                }
                +circleLayer("layer-1", "users") {
                }
            }
        ) {
            val positionChangedListener = onIndicatorPositionChangedListener(mapView)
            initLocationComponent(mapView, context, positionChangedListener)
            setupGesturesListener(mapView, positionChangedListener)
        }
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
        mapView.gestures.addOnMoveListener(
            onMoveListener(
                mapView,
                onIndicatorPositionChangedListener
            )
        )
    }

    private fun onCameraTrackingDismissed(
        mapView: MapView,
        onMoveListener: OnMoveListener
    ) {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener(mapView))
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun onIndicatorPositionChangedListener(
        mapView: MapView,
    ) = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().flyTo(CameraOptions.Builder().center(it).zoom(13.0).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private fun onCameraTrackingDismissed(
        mapView: MapView,
        onMoveListener: OnMoveListener,
        onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener,
    ) {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent(
        mapView: MapView,
        context: Context,
        onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener,
    ) {
        val locationComponentPlugin = mapView.location2
        locationComponentPlugin.puckBearingEnabled = false

        locationComponentPlugin.updateSettings {
            pulsingEnabled = false
            enabled = true
            locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mapbox_user_icon
                ),
                shadowImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mapbox_user_icon_shadow
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

}