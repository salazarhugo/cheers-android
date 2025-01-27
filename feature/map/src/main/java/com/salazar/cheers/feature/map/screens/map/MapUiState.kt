package com.salazar.cheers.feature.map.screens.map

import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.Theme
import com.salazar.cheers.core.Post

sealed interface MapUiState {
    data class NotInitialized(
        val isLoading: Boolean,
    ): MapUiState

    data class Initialized(
        val theme: Theme,
        val geojson: FeatureCollection?,
        val users: List<com.salazar.cheers.data.map.UserLocation>,
        val posts: List<Post>?,
        val city: String,
        val selected: MapAnnotation?,
        val isLoading: Boolean,
        val isPublic: Boolean,
        val errorMessages: List<String>,
        val searchInput: String,
        val userLocation: com.salazar.cheers.data.map.UserLocation,
        val ghostMode: Boolean,
    ): MapUiState
}