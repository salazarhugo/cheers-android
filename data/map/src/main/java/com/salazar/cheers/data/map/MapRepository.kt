package com.salazar.cheers.data.map

import com.salazar.cheers.core.model.SearchSuggestion
import com.salazar.cheers.shared.util.result.DataError

interface MapRepository {
    suspend fun updateGhostMode(
        ghostMode: Boolean,
    ): Result<Unit>

    suspend fun getLocationName(
        longitude: Double,
        latitude: Double,
        zoom: Double? = null,
    ): com.salazar.cheers.shared.util.result.Result<List<String>, DataError>

    suspend fun updateLocation(
        longitude: Double,
        latitude: Double,
    ): Result<Unit>

    suspend fun listFriendLocation(): Result<List<UserLocation>>

    suspend fun listSearchLocation(
        query: String,
    ): com.salazar.cheers.shared.util.result.Result<List<SearchSuggestion>, DataError>
}