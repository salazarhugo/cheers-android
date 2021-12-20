package com.salazar.cheers.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OtherProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val user: User

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User
    ) : OtherProfileUiState

    data class HasPosts(
        val posts: List<Post>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
) {
    fun toUiState(): OtherProfileUiState =
        if (posts == null || posts.isEmpty()) {
            OtherProfileUiState.NoPosts(
                user = user ?: User(),
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing
            )
        } else {
            OtherProfileUiState.HasPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                posts = posts,
                user = user ?: User(),
            )
        }
}

@HiltViewModel
class OtherProfileViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(OtherProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
//        refreshUser("")
    }

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    fun followUser(userId: String) {
        viewModelState.update {
            it.copy(isFollowing = true)
        }

        viewModelScope.launch {
            Neo4jUtil.followUser(userId)
        }
    }

    fun refreshUser(otherUserId: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getUser(otherUserId)) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }
}