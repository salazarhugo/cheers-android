package com.salazar.cheers.ui.main.map

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val postRepository: PostRepository,
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
    }

    private fun refreshPosts() {
        viewModelState.update { it.copy(isLoading = true) }

        val privacy = if (uiState.value.isPublic) Privacy.PUBLIC else Privacy.FRIENDS

        viewModelScope.launch {
            val posts = postRepository.getMapPosts(privacy = privacy)
            updateMapPosts(posts)
        }
    }

    private fun updateMapPosts(mapPosts: List<PostFeed>) {
        viewModelState.update {
            it.copy(posts = mapPosts, isLoading = false)
        }
    }


    fun updateCity(city: String) {
        viewModelState.update {
            it.copy(city = city)
        }
    }

    fun selectPost(post: PostFeed) {
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

data class MapUiState(
    val users: List<User> = emptyList(),
    val posts: List<PostFeed>? = null,
    val city: String = "",
    val selectedPost: PostFeed? = null,
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,
    val postSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
)

