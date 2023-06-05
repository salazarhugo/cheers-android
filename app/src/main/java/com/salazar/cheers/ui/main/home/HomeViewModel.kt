package com.salazar.cheers.ui.main.home

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.data.paging.DefaultPaginator
import com.salazar.cheers.core.domain.model.UserWithStories
import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val posts: List<com.salazar.cheers.data.post.repository.Post> = emptyList(),
    val userWithStoriesList: List<UserWithStories> = emptyList(),
    val listState: LazyListState = LazyListState(),
    val likes: Set<String> = emptySet(),
    val user: User? = null,
    val postSheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    val suggestions: List<com.salazar.cheers.core.model.UserItem>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchInput: String = "",
    val nativeAd: NativeAd? = null,
    val selectedTab: Int = 0,
    val notificationCount: Int = 0,
    val endReached: Boolean = false,
    val page: Int = 0,
    val storyPage: Int = 0,
    val storyEndReached: Boolean = false,
    val unreadChatCounter: Int = 0,
    val notes: List<Note> = emptyList(),
    val yourNote: Note? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val postRepository: com.salazar.cheers.data.post.repository.PostRepository,
    private val noteRepository: NoteRepository,
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    private val homeUseCases: HomeUseCases,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())
    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    private val storyPaginator = DefaultPaginator(
        initialKey = 0,
        onLoadUpdated = {
            updateIsLoading(isLoading = it)
        },
        onRequest = { nextPage ->
            storyRepository.fetchFeedStory(nextPage, 10)
        },
        getNextKey = {
            uiState.value.storyPage + 1
        },
        onError = {
            updateError("Couldn't load stories")
        },
        onSuccess = { items, newKey ->
            viewModelState.update {
                it.copy(
                    storyPage = newKey,
                    storyEndReached = items.isEmpty(),
                )
            }
        }
    )

    private val paginator = DefaultPaginator(
        initialKey = 0,
        onLoadUpdated = {
            updateIsLoading(isLoading = it)
        },
        onRequest = { nextPage ->
            updateIsLoading(true)
            postRepository.getPostFeed(nextPage, 5)
        },
        getNextKey = {
            uiState.value.page + 1
        },
        onError = {
            updateError("Couldn't refresh feed")
            updateIsLoading(false)
        },
        onSuccess = { items, newKey ->
            if (items.isEmpty()) {
                refreshSuggestions()
            }
            viewModelState.update {
                it.copy(
                    page = newKey,
                    endReached = items.isEmpty(),
//                    posts = it.posts + items,
                    isLoading = false,
                )
            }
        }
    )

    lateinit var postID: String

    init {
        stateHandle.get<String>("postID")?.let {
            postID = it
        }
        loadNextPosts()
        loadNextStories()
        refreshFriendNotes()

        viewModelScope.launch {
            userRepository.getCurrentUserFlow()
                .collect(::updateUser)
        }

        viewModelScope.launch {
            homeUseCases.getNotificationCounter()
                .collect(::updateNotificationCounter)
        }

        viewModelScope.launch {
            homeUseCases.getUnreadChatCounter()
                .collect(::updateUnreadChatCounter)
        }

        viewModelScope.launch {
            homeUseCases.listPostFeed()
                .collect(::updatePosts)
        }

        viewModelScope.launch {
            homeUseCases.listStoryFeed()
                .collect(::updateStories)
        }

        viewModelScope.launch {
            noteRepository.listFriendNotes()
                .collect(::updateNotes)
        }

        viewModelScope.launch {
            noteRepository.getYourNote()
                .collect(::updateYourNote)
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            homeUseCases.listSuggestions().onSuccess {
                updateSuggestions(it)
            }
        }
    }

    private fun updateSuggestions(suggestions: List<com.salazar.cheers.core.model.UserItem>) {
        viewModelState.update {
            it.copy(suggestions = suggestions)
        }
    }



    private fun refreshFriendNotes() {
        viewModelScope.launch {
            noteRepository.refreshFriendNotes()
        }
    }

    private fun updateNotificationCounter(count: Int) {
        viewModelState.update {
            it.copy(notificationCount = count)
        }
    }

    private fun updateUnreadChatCounter(unreadChatCounter: Int) {
        viewModelState.update {
            it.copy(unreadChatCounter = unreadChatCounter)
        }
    }

    private fun updateStories(userWithStoriesList: List<UserWithStories>) {
        viewModelState.update {
            it.copy(userWithStoriesList = userWithStoriesList)
        }
    }

    private fun updateYourNote(note: Note?) {
        viewModelState.update {
            it.copy(yourNote = note)
        }
    }

    private fun updateNotes(notes: List<Note>) {
        viewModelState.update {
            it.copy(notes = notes)
        }
    }

    private fun loadNextStories() {
        viewModelScope.launch {
            storyPaginator.loadNextItems()
        }
    }

    fun loadNextPosts() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun updatePosts(posts: List<com.salazar.cheers.data.post.repository.Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    fun onSwipeRefresh() {
        updateIsLoading(true)
        viewModelScope.launch {
            userRepository.getUserSignIn(userId = FirebaseAuth.getInstance().currentUser?.uid!!)
                .collect {
                    if (it is Resource.Success)
                        updateUser(user = it.data)
                }
        }
        viewModelScope.launch {
            paginator.reset()
            paginator.loadNextItems()
        }
        viewModelScope.launch {
            storyPaginator.reset()
            storyPaginator.loadNextItems()
        }
        refreshFriendNotes()
    }

    private fun updateError(message: String?) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateUser(user: User?) {
        viewModelState.update {
            it.copy(user = user)
        }
    }

    fun toggleLike(post: com.salazar.cheers.data.post.repository.Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            userRepository.blockUser(userId = userId)
        }
    }

    private fun setNativeAd(nativeAd: NativeAd) {
        viewModelState.update {
            it.copy(nativeAd = nativeAd)
        }
    }

    fun initNativeAdd(context: Context) {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("2C6292E9B3EBC9CF72C85D55627B6D2D")).build()
        MobileAds.setRequestConfiguration(configuration)
        val adLoader = AdLoader.Builder(context, "ca-app-pub-7182026441345500/3409583237")
            .forNativeAd { ad: NativeAd ->
                setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    print(adError.message)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun onAddFriendClick(userID: String) {
        viewModelScope.launch {
            homeUseCases.sendFriendRequest(userId = userID).onSuccess {}
        }
    }
}

sealed class HomeUIAction {
    object OnChatClick : HomeUIAction()
    object OnActivityClick : HomeUIAction()
    object OnSearchClick : HomeUIAction()
    object OnSwipeRefresh : HomeUIAction()
    object OnAddStoryClick : HomeUIAction()
    object OnCreatePostClick : HomeUIAction()
    object OnLoadNextItems : HomeUIAction()
    object OnCreateNoteClick : HomeUIAction()
    data class OnCommentClick(val postID: String) : HomeUIAction()
    data class OnShareClick(val postID: String) : HomeUIAction()
    data class OnLikeClick(val post: com.salazar.cheers.data.post.repository.Post) : HomeUIAction()
    data class OnStoryFeedClick(val page: Int) : HomeUIAction()
    data class OnStoryClick(val userID: String) : HomeUIAction()
    data class OnUserClick(val userID: String) : HomeUIAction()
    data class OnPostClick(val postID: String) : HomeUIAction()
    data class OnNoteClick(val userID: String) : HomeUIAction()
    data class OnPostMoreClick(val postID: String) : HomeUIAction()
    data class OnAddFriendClick(val userID: String) : HomeUIAction()
}
