package com.salazar.cheers.feature.post_likes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_post_likes.ListPostLikesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostLikesUiState(
    val isLoading: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val users: List<UserItem>? = null,
)

@HiltViewModel
class PostLikesViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val listPostLikesUseCase: ListPostLikesUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PostLikesUiState(isLoading = true))
    lateinit var postID: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("postID")?.let { postID ->
            this.postID = postID
            refreshPostLikes(postID)
        }
    }

     fun onPullRefresh() {
         refreshPostLikes(postID)
     }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isRefreshing)
        }
    }

    private fun refreshPostLikes(postID: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            listPostLikesUseCase(postID).collect(::updateUsers)
        }
    }

    private fun updateUsers(users: List<UserItem>) {
        viewModelState.update {
            it.copy(users = users, isLoading = false)
        }
    }
}