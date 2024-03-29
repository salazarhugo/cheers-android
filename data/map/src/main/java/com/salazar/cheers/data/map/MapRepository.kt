package com.salazar.cheers.data.map

import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun updateGhostMode(
        ghostMode: Boolean,
    ): Result<Unit>

    fun getLocationName(
        longitude: Double,
        latitude: Double,
        zoom: Double? = null,
    ): Flow<List<String>>

    suspend fun updateLocation(
        longitude: Double,
        latitude: Double,
    ): Result<Unit>

    suspend fun listFriendLocation(): Result<List<UserLocation>>
}