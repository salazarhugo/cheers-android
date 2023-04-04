package com.salazar.cheers.map.screens.map

import android.util.Log
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.salazar.cheers.Settings
import com.salazar.cheers.core.data.datastore.DataStoreRepository
import com.salazar.cheers.core.data.location.DefaultLocationClient
import com.salazar.cheers.map.data.repository.MapRepository
import com.salazar.cheers.post.data.repository.PostRepository
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.data.internal.Privacy
import com.salazar.cheers.map.domain.models.UserLocation
import com.salazar.cheers.map.domain.usecase.update_location.UpdateLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


enum class MapAnnotationType {
    POST,
    USER
}

data class MapUiState(
    val geojson: FeatureCollection? = null,
    val users: List<UserLocation> = emptyList(),
    val posts: List<Post>? = null,
    val city: String = "",
    val selectedPost: Post? = null,
    val selectedUser: UserLocation? = null,
    val selected: MapAnnotationType? = null,
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,
    val sheetState: BottomSheetState = BottomSheetState(BottomSheetValue.Collapsed),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val mapboxMap: MapboxMap? = null,
    val userLocation: UserLocation? = null,
    val ghostMode: Boolean = false,
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val postRepository: PostRepository,
    val mapRepository: MapRepository,
    private val locationClient: DefaultLocationClient,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        initUserLocation()
        updateLocation()
        refreshPosts()
        refreshFriendsLocation()
        viewModelScope.launch {
            dataStoreRepository.userPreferencesFlow.collect(::updateSettings)
        }
    }

    private fun updateSettings(settings: Settings) {
        val ghostMode = settings.ghostMode
        viewModelState.update {
            it.copy(ghostMode = ghostMode)
        }
        refreshFriendsLocation()
    }

    private fun initUserLocation() {
        viewModelScope.launch {
            val location = locationClient.getLastKnownLocation() ?: return@launch
            val userLocation = UserLocation(
                id = "",
                latitude = location.latitude,
                longitude = location.longitude,
                name = "",
                locationName = "",
                lastUpdated = Date().time,
                username = "",
                picture = "",
                verified = false,
            )
            viewModelState.update {
                it.copy(userLocation = userLocation)
            }
        }
    }

    private fun updateLocation() {
        viewModelScope.launch {
            updateLocationUseCase()
        }
    }

    private fun refreshFriendsLocation() {
        viewModelScope.launch {
            mapRepository.listFriendLocation().onSuccess {
                Log.d("SURE", it.toString())
                updateUserLocation(it)
            }
        }
    }

    private fun updateUserLocation(userLocation: List<UserLocation>) {
        viewModelState.update {
            it.copy(users = userLocation)
        }
    }

    fun onMapReady(map: MapView) {
        viewModelState.update {
            it.copy(mapboxMap = map.getMapboxMap())
        }
    }

    private fun refreshPosts() {
        viewModelState.update {
            it.copy(isLoading = true)
        }

        val privacy = if (uiState.value.isPublic)
            Privacy.PUBLIC
        else
            Privacy.FRIENDS

        viewModelScope.launch {
            postRepository.listMapPost(privacy = privacy)
                .collect(::updateMapPosts)
        }
    }

    private fun updateMapPosts(mapPosts: List<Post>) {
        viewModelState.update {
            it.copy(posts = mapPosts, isLoading = false)
        }
    }

    fun updateCity(city: String) {
        viewModelState.update {
            it.copy(city = city)
        }
    }

    fun onUserViewAnnotationClick(userLocation: UserLocation) {
        viewModelState.update {
            it.copy(selectedUser = userLocation, selected = MapAnnotationType.USER)
        }
    }

    fun selectPost(post: Post) {
        viewModelState.update {
            it.copy(selectedPost = post, selected = MapAnnotationType.POST)
        }
    }

    fun onTogglePublic() {
        viewModelState.update {
            it.copy(isPublic = !it.isPublic)
        }
        refreshPosts()
    }

    fun onMyLocationClick() {
    }
}

