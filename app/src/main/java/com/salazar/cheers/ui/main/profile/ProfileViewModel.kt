package com.salazar.cheers.ui.main.profile

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {

    val isLoading: Boolean
    val errorMessages: String
    val sheetState: ModalBottomSheetState

    data class Loading(
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val sheetState: ModalBottomSheetState,
    ) : ProfileUiState

    data class HasUser(
        val user: User,
        val postFlow: Flow<PagingData<PostFeed>>,
        override val sheetState: ModalBottomSheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
    ) : ProfileUiState
}

private data class ProfileViewModelState @ExperimentalMaterialApi constructor(
    val user: User? = null,
    val posts: Flow<PagingData<PostFeed>> = emptyFlow(),
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
            ProfileUiState.HasUser(
                postFlow = posts,
                user = user,
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
            )
        else
            ProfileUiState.Loading(
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
            )
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = false))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            userRepository.getUserFlow(FirebaseAuth.getInstance().currentUser?.uid!!)
                .collect { user ->
                    viewModelState.update {
                        it.copy(user = user)
                    }
                }
        }
        refreshUserPosts()
    }

    fun refresh() {
        refreshUser()
        refreshUserPosts()
    }

    fun toggleLike(
        post: Post,
    ) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = userRepository.refreshUser(FirebaseAuth.getInstance().currentUser?.uid!!)
            when (result) {
                is Result.Success -> viewModelState.update {
                    it.copy(user = result.data, isLoading = false)
                }
                is Result.Error -> viewModelState.update {
                    it.copy(
                        errorMessages = "Couldn't refresh",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun refreshUserPosts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        viewModelScope.launch {
            val posts = postRepository.profilePostFeed(userIdOrUsername = userId)
            viewModelState.update {
                it.copy(posts = posts, isLoading = false)
            }
        }
    }

}