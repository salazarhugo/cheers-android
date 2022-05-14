package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.salazar.cheers.ChatServiceGrpcKt
import com.salazar.cheers.GetRoomIdReq
import com.salazar.cheers.backend.ChatService
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.remote.ErrorHandleInterceptor
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OtherProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val shortLink: String?

    data class NoUser(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val shortLink: String?,
    ) : OtherProfileUiState

    data class HasUser(
        val postFlow: Flow<PagingData<PostFeed>> = emptyFlow(),
        val user: User,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val shortLink: String?,
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val postFlow: Flow<PagingData<PostFeed>> = emptyFlow(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
) {
    fun toUiState(): OtherProfileUiState =
        if (user != null) {
            OtherProfileUiState.HasUser(
                user = user,
                postFlow = postFlow,
                isLoading = isLoading,
                errorMessages = errorMessages,
                shortLink = shortLink,
            )
        } else {
            OtherProfileUiState.NoUser(
                isLoading = isLoading,
                errorMessages = errorMessages,
                shortLink = shortLink,
            )
        }
}

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(OtherProfileViewModelState(isLoading = true))
    lateinit var chatService: ChatService

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                userRepository.getUserFlow(userIdOrUsername = username).collect { user ->
                    updateUser(user)
                }
            }
        }
        refresh()

        viewModelScope.launch(Dispatchers.IO) {
            val user2 =
                FirebaseAuth.getInstance().currentUser ?: throw Exception("User is not logged in.")
            val task: Task<GetTokenResult> = user2.getIdToken(false)
            val tokenResult = Tasks.await(task)
            val idToken = tokenResult.token ?: throw Exception("idToken is null")

            val managedChannel = ManagedChannelBuilder
                .forAddress("chat-r3a2dr4u4a-nw.a.run.app", 443)
                .build()

            val client = ChatServiceGrpcKt
                .ChatServiceCoroutineStub(managedChannel)
                .withInterceptors(ErrorHandleInterceptor(idToken = idToken))


            chatService = ChatService(client)
        }
    }

    fun getRoomId(onSuccess: (String) -> Unit) {
        val otherUserId = viewModelState.value.user?.id ?: return

        viewModelScope.launch {
            onSuccess(
                chatService.getRoomId(
                    GetRoomIdReq.newBuilder().setRecipientId(otherUserId).build()
                ).roomId
            )
        }
    }

    fun refresh() {
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                userRepository.refreshUser(userIdOrUsername = username)
                refreshUserPosts(username = username)
            }
        }
    }


    fun toggleFollow(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user = user)
        }
    }

    fun updateUser(user: User?) {
        viewModelState.update {
            it.copy(user = user)
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    fun updateShortLink(shortLink: String) {
        viewModelState.update {
            it.copy(shortLink = shortLink)
        }
    }

    private fun refreshUserPosts(username: String) {
        viewModelScope.launch {
            val posts = postRepository.profilePostFeed(userIdOrUsername = username)
            viewModelState.update {
                it.copy(postFlow = posts, isLoading = false)
            }
        }
    }
}