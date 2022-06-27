package com.salazar.cheers.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.MapRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.internal.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class MapPostHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val posts: List<Post>? = null,
)

@HiltViewModel
class MapPostHistoryViewModel @Inject constructor(
    postRepository: PostRepository,
    public val mapRepository: MapRepository,
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

