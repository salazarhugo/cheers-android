package com.salazar.cheers.feature.map.screens.map

import androidx.compose.material3.SheetState
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.core.Post

sealed interface MapUiState {
    data class NotInitialized(
        val isLoading: Boolean,
    ): MapUiState

    data class Initialized(
        val geojson: FeatureCollection?,
        val users: List<com.salazar.cheers.data.map.UserLocation>,
        val posts: List<Post>?,
        val city: String,
        val selected: MapAnnotation?,
        val isLoading: Boolean,
        val isPublic: Boolean,
        val sheetState: SheetState = SheetState(skipPartiallyExpanded = true),
        val errorMessages: List<String>,
        val searchInput: String,
        val userLocation: com.salazar.cheers.data.map.UserLocation,
        val ghostMode: Boolean,
    ): MapUiState
}