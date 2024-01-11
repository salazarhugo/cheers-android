package com.salazar.cheers.feature.map.data.repository

import android.content.Context
import android.util.Log
import cheers.location.v1.ListFriendLocationRequest
import cheers.location.v1.LocationServiceGrpcKt
import cheers.location.v1.UpdateGhostModeRequest
import cheers.location.v1.UpdateLocationRequest
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.salazar.cheers.feature.map.data.mapper.toUserLocation
import com.salazar.cheers.feature.map.domain.models.UserLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MapRepositoryImpl @Inject constructor(
    private val locationService: LocationServiceGrpcKt.LocationServiceCoroutineStub,
) : MapRepository {
    override suspend fun updateGhostMode(
        ghostMode: Boolean,
    ): Result<Unit> {
        return try {
            val request = UpdateGhostModeRequest.newBuilder()
                .setGhostMode(ghostMode)
                .build()

            val response = locationService.updateGhostMode(request)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateLocation(
        longitude: Double,
        latitude: Double,
    ): Result<Unit> {
        return try {
            val request = UpdateLocationRequest.newBuilder()
                .setLongitude(longitude)
                .setLatitude(latitude)
                .build()

            val response = locationService.updateLocation(request)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun listFriendLocation(): Result<List<UserLocation>> {
        return try {
            val request = ListFriendLocationRequest.newBuilder().build()

            val locations = locationService.listFriendLocation(request)
            val userLocation = locations.itemsList.map {
                it.toUserLocation()
            }
            Result.success(userLocation)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }



//    private lateinit var reverseGeocoding: SearchEngine
//    private lateinit var searchRequestTask: AsyncOperationTask
//
//    val searchCallback = object : SearchCallback {
//        override fun onError(e: Exception) {
//            Log.i("SearchApiExample", "Reverse geocoding error", e)
//        }
//
//        override fun onResults(
//            results: List<SearchResult>,
//            responseInfo: com.mapbox.search.ResponseInfo
//        ) {
//            if (results.isEmpty()) {
//                Log.i("SearchApiExample", "No reverse geocoding results")
//            } else {
////                updateCity(results[0].name)
//                Log.i("SearchApiExample", "Reverse geocoding results: $results")
//            }
//        }
//    }

    override suspend fun onMapReady(
        mapView: MapView,
        context: Context
    ) = withContext(Dispatchers.Main) {
//        reverseGeocoding = SearchEngine.createSearchEngine(
//            SearchEngineSettings(context.getString(R.string.mapbox_access_token))
//        )
        mapView.gestures.rotateEnabled = false
        mapView.attribution.enabled = false
        mapView.scalebar.enabled = false

        mapView.mapboxMap.flyTo(
            CameraOptions.Builder()
                .zoom(1.0)
                .build()
        )

        val style =
//            if (context.isDarkModeOn())
            "mapbox://styles/salazarbrock/ckxuwlu02gjiq15p3iknr2lk0"
//            else
//                "mapbox://styles/salazarbrock/ckzsmluho004114lmeb8rl2zi"

        mapView.mapboxMap.loadStyle(
            com.mapbox.maps.extension.style.style(style = style) {}
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
            val cameraState = mapView.mapboxMap.cameraState
            val center = cameraState.center

//            val queryType = when {
//                cameraState.zoom < 5.0 -> QueryType.COUNTRY
//                cameraState.zoom < 10.0 -> QueryType.REGION
//                else -> QueryType.PLACE
//            }
//
//            val options = ReverseGeoOptions(
//                center = center,
//                types = listOf(queryType)
//            )
//            searchRequestTask = reverseGeocoding.search(options, searchCallback)
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
        mapView.mapboxMap.flyTo(CameraOptions.Builder().center(it).zoom(13.0).build())
        mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
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
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.puckBearingEnabled = false

        locationComponentPlugin.updateSettings {
            pulsingEnabled = true
            enabled = true
//            locationPuck = LocationPuck2D(
//                topImage = AppCompatResources.getDrawable(
//                    context,
//                    R.drawable.mapbox_user_icon
//                ),
//                shadowImage = AppCompatResources.getDrawable(
//                    context,
//                    R.drawable.mapbox_user_icon_shadow
//                ),
//                scaleExpression = interpolate {
//                    linear()
//                    zoom()
//                    stop {
//                        literal(0.0)
//                        literal(0.6)
//                    }
//                    stop {
//                        literal(20.0)
//                        literal(1.0)
//                    }
//                }.toJson()
//            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
    }

}