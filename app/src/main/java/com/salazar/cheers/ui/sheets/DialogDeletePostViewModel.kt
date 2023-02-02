package com.salazar.cheers.ui.sheets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
)

@HiltViewModel
class DialogDeletePostViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())
    lateinit var postID: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("postID")?.let {
            postID = it
        }
    }

    fun deletePost(onCompleted: () -> Unit) {
        if (!::postID.isInitialized)
            return

        viewModelScope.launch {
            postRepository.deletePost(postId = postID)
            onCompleted()
        }
    }
}