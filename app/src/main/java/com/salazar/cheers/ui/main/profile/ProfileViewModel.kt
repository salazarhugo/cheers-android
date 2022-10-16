package com.salazar.cheers.ui.main.profile

import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.party.v1.FeedPartyRequest
import cheers.party.v1.PartyServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.repository.PartyRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        val posts: List<Post>?,
        val parties: List<Party>?,
        override val sheetState: ModalBottomSheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
            ProfileUiState.HasUser(
                posts = posts,
                user = user,
                parties = parties,
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
    private val partyRepository: PartyRepository,
    private val partyStub: PartyServiceGrpcKt.PartyServiceCoroutineStub,
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
            val response = partyStub.feedParty(FeedPartyRequest.newBuilder().setPageSize(20).build())
            Log.d("GRPC", response.toString())
        }
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect { user ->
                updateUser(user)
            }
        }
        refreshUser()
        refreshUserPosts()
    }

    fun onSwipeRefresh() {
        refreshUser()
        refreshUserPosts()
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

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    private fun refreshUserPosts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.profilePost(userIdOrUsername = userId).collect {
                updatePosts(it)
            }
        }
    }
}