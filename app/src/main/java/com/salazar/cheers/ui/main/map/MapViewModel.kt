package com.salazar.cheers.ui.main.map

import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.data.repository.MapRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MapUiState(
    val geojson: FeatureCollection? = null,
    val users: List<Feature> = emptyList(),
    val posts: List<Post>? = null,
    val city: String = "",
    val selectedPost: Post? = null,
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,
    val postSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    val mapRepository: MapRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshPosts()
        viewModelScope.launch {
            val geojson = userRepository.getLocations()
            if (geojson?.features() != null)
                viewModelState.update { it.copy(users = geojson.features()!!.toList()) }
            Log.d("HAHA", uiState.value.geojson?.features()?.size.toString())
        }
    }

    private fun refreshPosts() {
        viewModelState.update { it.copy(isLoading = true) }

        val privacy = if (uiState.value.isPublic) Privacy.PUBLIC else Privacy.FRIENDS

        viewModelScope.launch {
            val posts = postRepository.getMapPosts(privacy = privacy)
            updateMapPosts(posts)
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

    fun selectPost(post: Post) {
        viewModelState.update {
            it.copy(selectedPost = post)
        }
    }

    fun onTogglePublic() {
        viewModelState.update {
            it.copy(isPublic = !it.isPublic)
        }
        refreshPosts()
    }
}
