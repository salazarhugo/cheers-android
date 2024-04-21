package com.salazar.cheers.ui.sheets.post_more

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val postRepository: com.salazar.cheers.data.post.repository.PostRepository,
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