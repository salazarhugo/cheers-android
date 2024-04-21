package com.salazar.cheers.data.map

import android.content.Context
import android.util.Log
import cheers.location.v1.GeocodeRequest
import cheers.location.v1.ListFriendLocationRequest
import cheers.location.v1.LocationServiceGrpcKt
import cheers.location.v1.UpdateGhostModeRequest
import cheers.location.v1.UpdateLocationRequest
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.QueryType
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.common.AsyncOperationTask
import com.mapbox.search.result.SearchResult
import com.salazar.common.util.result.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MapRepositoryImpl @Inject constructor(
    private val locationService: LocationServiceGrpcKt.LocationServiceCoroutineStub,
    private val searchEngine: SearchEngine,
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


    override suspend fun getLocationName(
        longitude: Double,
        latitude: Double,
        zoom: Double?,
    ): com.salazar.common.util.result.Result<List<String>, DataError> {

        val queryType = if (zoom == null) {
            QueryType.PLACE
        } else {
            when {
                zoom < 5.0 -> QueryType.COUNTRY
                zoom < 10.0 -> QueryType.REGION
                else -> QueryType.PLACE
            }
        }

        val request = GeocodeRequest.newBuilder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .build()

        return try {
            val locations = locationService.geocode(request)
            val userLocation = locations.locationsList.map {
                it.name
            }
            com.salazar.common.util.result.Result.Success(userLocation)
        } catch (e: Exception) {
            e.printStackTrace()
            com.salazar.common.util.result.Result.Error(DataError.Network.UNKNOWN)
        }
    }

}