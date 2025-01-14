package com.salazar.cheers.core.ui.components.post.mentions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_post_likes.ListPostMentionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MentionsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val users: List<UserItem> = emptyList(),
)

@HiltViewModel
class MentionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listPostMentionsUseCase: ListPostMentionsUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MentionsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun listPostMentions(postID: String) {
        viewModelScope.launch {
            listPostMentionsUseCase(
                postID = postID,
            ).collect(::updateUsers)
        }
    }

    fun updateUsers(users: List<UserItem>) {
        viewModelState.update {
            it.copy(users = users)
        }
    }
}
