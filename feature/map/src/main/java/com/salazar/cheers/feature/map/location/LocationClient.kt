package com.salazar.cheers.feature.map.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    suspend fun getLastKnownLocation(): Location?

    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}