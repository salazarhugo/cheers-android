package com.salazar.cheers.ui.map

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

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

        viewModelScope.launch {
            viewModelState.update {
                val result = Neo4jUtil.getMapPosts(it.isPublic)
                when (result) {
                    is Result.Success -> it.copy(posts = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
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

data class MapUiState(
    val users: List<User> = emptyList(),
    val posts: List<Post>? = null,
    val city: String = "",
    val selectedPost: Post? = null,
    val isLoading: Boolean = false,
    val isPublic: Boolean = false,
    val postSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
)

