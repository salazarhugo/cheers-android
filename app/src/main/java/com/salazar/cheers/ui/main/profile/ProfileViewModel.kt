package com.salazar.cheers.ui.main.profile

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.backend.Neo4jUtil.updateUser
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Event
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
        val postFlow: Flow<PagingData<Post>>,
        val events: List<Event>?,
        override val sheetState: ModalBottomSheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val user: User? = null,
    val posts: Flow<PagingData<Post>> = emptyFlow(),
    val events: List<Event>? = null,
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
            ProfileUiState.HasUser(
                postFlow = posts,
                user = user,
                events = events,
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
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect { user ->
                updateUser(user)
            }
        }
        refreshUser()
        getUserEvents()
        refreshUserPosts()
    }

    fun onSwipeRefresh() {
        refreshUser()
        refreshUserPosts()
        viewModelScope.launch {
            eventRepository.refreshMyEvents()
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user, isLoading = false)
        }
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
            userRepository.fetchUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        }
    }

    private fun refreshUserPosts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        viewModelScope.launch {
            val posts = postRepository.profilePost(userIdOrUsername = userId)
            viewModelState.update {
                it.copy(posts = posts, isLoading = false)
            }
        }
    }

    private fun getUserEvents() {
        viewModelScope.launch {
            eventRepository.getEvents().collect { events ->
                viewModelState.update {
                    it.copy(events = events)
                }
            }
        }
    }

}