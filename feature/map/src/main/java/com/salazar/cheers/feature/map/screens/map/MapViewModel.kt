package com.salazar.cheers.feature.map.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Settings
import com.salazar.cheers.core.Post
import com.salazar.cheers.data.map.UserLocation
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.get_last_known_location.GetLastKnownLocationUseCase
import com.salazar.cheers.domain.get_location_name.GetLocationNameUseCase
import com.salazar.cheers.domain.get_location_updates.GetLocationUpdatesUseCase
import com.salazar.cheers.domain.list_map_post.ListMapPostUseCase
import com.salazar.cheers.feature.map.domain.usecase.update_location.UpdateLocationUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
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
        val user: UserLocation,
    ) : MapAnnotation()
}

@HiltViewModel
class MapViewModel @Inject constructor(
    val mapRepository: com.salazar.cheers.data.map.MapRepositoryImpl,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val dataStoreRepository: DataStoreRepository,
    private val locationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val accountUseCase: GetAccountUseCase,
    private val listMapPostUseCase: ListMapPostUseCase,
    private val getLocationsUseCase: GetLocationNameUseCase,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
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
//        viewModelScope.launch {
//            getCurrentCityFlowUseCase().collect(::updateCity)
//        }
        viewModelScope.launch {
            dataStoreRepository.userPreferencesFlow.collect(::updateSettings)
        }
        viewModelScope.launch {
            val location = getLastKnownLocationUseCase() ?: return@launch
            val result = getLocationsUseCase(location.longitude, location.latitude).getOrNull()
            viewModelState.update {
                it.copy(city = result?.firstOrNull().orEmpty())
            }
        }
    }

    private fun updateSettings(settings: Settings) {
        val ghostMode = settings.ghostMode
        viewModelState.update {
            it.copy(
                ghostMode = ghostMode,
                isDarkMode = settings.theme,
            )
        }
        refreshFriendsLocation()
    }

    private fun initUserLocation() {
        viewModelScope.launch {
            val account = accountUseCase().firstOrNull() ?: return@launch
            locationUpdatesUseCase(interval = 2000)
                .collectLatest { location ->
                    val userLocation = UserLocation(
                        id = account.id,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        name = account.name,
                        username = account.username,
                        locationName = "",
                        lastUpdated = Date().time / 1000,
                        picture = account.picture,
                        verified = account.verified,
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

    private fun updateUserLocation(userLocation: List<UserLocation>) {
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

    fun onUserViewAnnotationClick(userLocation: UserLocation) {
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

