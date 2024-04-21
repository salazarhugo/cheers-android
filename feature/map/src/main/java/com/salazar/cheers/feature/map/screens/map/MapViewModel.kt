package com.salazar.cheers.feature.map.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Settings
import com.salazar.cheers.core.Post
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.get_location_updates.GetLocationUpdatesUseCase
import com.salazar.cheers.domain.list_map_post.ListMapPostUseCase
import com.salazar.cheers.feature.map.domain.usecase.update_location.UpdateLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


sealed class MapAnnotation {
    data class PostAnnotation(
        val post: Post,
    ) : MapAnnotation()

    data class UserAnnotation(
        val user: com.salazar.cheers.data.map.UserLocation
    ): MapAnnotation()
}

@HiltViewModel
class MapViewModel @Inject constructor(
    val mapRepository: com.salazar.cheers.data.map.MapRepositoryImpl,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val dataStoreRepository: DataStoreRepository,
    private val locationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val accountUseCase: GetAccountUseCase,
    private val listMapPostUseCase: ListMapPostUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState(),
        )

    init {
        initUserLocation()
        updateLocation()
        listenPosts()
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
            val account = accountUseCase().firstOrNull() ?: return@launch
            locationUpdatesUseCase(interval = 2000).collect { location ->
                val userLocation = com.salazar.cheers.data.map.UserLocation(
                    id = "",
                    latitude = location.latitude,
                    longitude = location.longitude,
                    name = account.name,
                    locationName = "",
                    lastUpdated = Date().time / 1000,
                    username = account.username,
                    picture = account.picture,
                    verified = false,
                )
                viewModelState.update {
                    it.copy(userLocation = userLocation)
                }
            }
        }
    }

    fun onPermissionGranted() {
        initUserLocation()
    }

    private fun updateLocation() {
        viewModelScope.launch {
            updateLocationUseCase()
        }
    }

    private fun listenPosts() {
        viewModelScope.launch {
            listMapPostUseCase().collect(::updatePosts)
        }
    }

    private fun updatePosts(posts: List<Post>?) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }


    private fun refreshFriendsLocation() {
        viewModelScope.launch {
            mapRepository.listFriendLocation().onSuccess {
                updateUserLocation(it)
            }
        }
    }

    private fun updateUserLocation(userLocation: List<com.salazar.cheers.data.map.UserLocation>) {
        viewModelState.update {
            it.copy(users = userLocation)
        }
    }

    fun updateCity(city: String) {
        viewModelState.update {
            it.copy(city = city)
        }
    }

    fun onDismissBottomSheet() {
        viewModelState.update {
            it.copy(selected = null)
        }
    }

    fun onUserViewAnnotationClick(userLocation: com.salazar.cheers.data.map.UserLocation) {
        viewModelState.update {
            it.copy(selected = MapAnnotation.UserAnnotation(userLocation))
        }
    }

    fun onPostViewAnnotationClick(post: Post) {
        viewModelState.update {
            it.copy(selected = MapAnnotation.PostAnnotation(post))
        }
    }

    fun onTogglePublic() {
        viewModelState.update {
            it.copy(isPublic = !it.isPublic)
        }
    }

    fun onMyLocationClick() {
    }
}

