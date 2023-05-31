package com.salazar.cheers.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.map.data.repository.MapRepository
import com.salazar.cheers.post.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapPostHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val posts: List<Post>? = null,
)

@HiltViewModel
class MapPostHistoryViewModel @Inject constructor(
    postRepository: PostRepository,
    val mapRepository: MapRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapPostHistoryUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            val posts = postRepository.getUserPosts()
            updatePosts(posts)
        }
    }

    private fun updatePosts(mapPosts: List<Post>) {
        viewModelState.update {
            it.copy(posts = mapPosts, isLoading = false)
        }
    }

}

