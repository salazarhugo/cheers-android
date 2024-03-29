package com.salazar.cheers.feature.map.screens.map

import androidx.compose.material3.SheetState
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.map.UserLocation

data class MapViewModelState(
    val geojson: FeatureCollection? = null,
    val users: List<com.salazar.cheers.data.map.UserLocation> = emptyList(),
    val posts: List<Post>? = null,
    val city: String = "",
    val selected: MapAnnotation? = null,
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,
    val sheetState: SheetState = SheetState(skipPartiallyExpanded = true),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val userLocation: com.salazar.cheers.data.map.UserLocation? = null,
    val ghostMode: Boolean = false,
) {
    fun toUiState(): MapUiState {
         return if (userLocation != null) {
            MapUiState.Initialized(
                geojson = geojson,
                users = users,
                posts = posts,
                city = city,
                selected = selected,
                isLoading = isLoading,
                isPublic = isPublic,
                sheetState = sheetState,
                errorMessages = errorMessages,
                searchInput = searchInput,
                userLocation = userLocation,
                ghostMode = ghostMode,
            )
        } else {
            MapUiState.NotInitialized(isLoading = false)
        }
    }
}