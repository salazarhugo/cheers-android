package com.salazar.cheers.feature.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.domain.feed_party.ListPartyFlowUseCase
import com.salazar.cheers.domain.list_post.ListPostFlowUseCase
import com.salazar.cheers.domain.list_post.ListPostUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val postRepository: PostRepository,
    private val listPostFlowUseCase: ListPostFlowUseCase,
    private val listPostUseCase: ListPostUseCase,
    private val listMyPartyFlowUseCase: ListPartyFlowUseCase,
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
            userRepositoryImpl.getCurrentUserFlow().collect { user ->
                updateUser(user)
            }
        }
        refreshUser()
        refreshUserPosts()
        viewModelScope.launch {
            listMyPartyFlowUseCase().collect(::updateMyParties)
        }
        viewModelScope.launch {
            listPostFlowUseCase().collect(::updatePosts)
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            updateIsRefreshing(true)
            refreshUser()
            refreshUserPosts()
            updateIsRefreshing(false)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateMyParties(parties: List<Party>) {
        viewModelState.update {
            it.copy(parties = parties)
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

        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepositoryImpl.fetchCurrentUser()
            when (result) {
                is Resource.Error -> updateError(result.message)
                is Resource.Loading -> {}
                is Resource.Success -> {}
            }
            viewModelState.update { it.copy(isLoading = false) }
        }
    }

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    private fun refreshUserPosts() {
        viewModelScope.launch {
            listPostUseCase(
                page = 1,
            )
        }
    }

    private fun updateError(message: String?) {
        if (message == null)
            return
        viewModelState.update {
            it.copy(errorMessages = message)
        }
    }
}