package com.salazar.cheers.feature.map.data.repository

import android.content.Context
import com.mapbox.maps.MapView
import com.salazar.cheers.feature.map.domain.models.UserLocation

interface MapRepository {
    suspend fun updateGhostMode(
        ghostMode: Boolean,
    ): Result<Unit>

    suspend fun updateLocation(
        longitude: Double,
        latitude: Double,
    ): Result<Unit>

    suspend fun listFriendLocation(): Result<List<UserLocation>>

    suspend fun onMapReady(
        mapView: MapView,
        context: Context
    )
}