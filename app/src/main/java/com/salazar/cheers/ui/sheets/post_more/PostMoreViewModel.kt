package com.salazar.cheers.ui.sheets.post_more

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostMoreUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val post: Post? = null,
)

@HiltViewModel
class PostMoreViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PostMoreUiState(isLoading = true))
    private lateinit var postId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("postID")?.let {
            postId = it
        }
        viewModelScope.launch {
            val post = postRepository.getPost(postId = postId)
            updatePost(post = post)
        }
    }

    private fun updatePost(post: Post?) {
        viewModelState.update {
            it.copy(post = post)
        }
    }
}