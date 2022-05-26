package com.salazar.cheers.ui.main.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String
    val suggestions: List<SuggestionUser>?
    val postSheetState: ModalBottomSheetState
    val nativeAd: NativeAd?
    val selectedTab: Int

    data class NoPosts(
        override val suggestions: List<SuggestionUser>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
        override val postSheetState: ModalBottomSheetState,
        override val nativeAd: NativeAd?,
        override val selectedTab: Int,
    ) : HomeUiState

    data class HasPosts(
        val postsFlow: Flow<PagingData<Post>>,
        val eventsFlow: Flow<PagingData<EventUi>>?,
        val storiesFlow: Flow<PagingData<Story>>?,
        val stories: PagingData<Story>?,
        val listState: LazyListState = LazyListState(),
        val likes: Set<String>,
        val user: User? = null,
        override val postSheetState: ModalBottomSheetState,
        override val suggestions: List<SuggestionUser>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
        override val nativeAd: NativeAd?,
        override val selectedTab: Int,
    ) : HomeUiState
}

private data class HomeViewModelState(
    val user: User? = null,
    val postsFlow: Flow<PagingData<Post>>? = null,
    val eventsFlow: Flow<PagingData<EventUi>>? = null,
    val storiesFlow: Flow<PagingData<Story>>? = null,
    val stories: PagingData<Story>? = null,
    val listState: LazyListState = LazyListState(),
    val suggestions: List<SuggestionUser>? = null,
    val likes: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    val nativeAd: NativeAd? = null,
    val selectedTab: Int = 0,
) {
    fun toUiState(): HomeUiState =
        if (postsFlow == null) {
            HomeUiState.NoPosts(
                nativeAd = nativeAd,
                postSheetState = sheetState,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
                selectedTab = selectedTab,
            )
        } else {
            HomeUiState.HasPosts(
                user = user,
                nativeAd = nativeAd,
                postsFlow = postsFlow,
                eventsFlow = eventsFlow,
                storiesFlow = storiesFlow,
                listState = listState,
                postSheetState = sheetState,
                likes = likes,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
                selectedTab = selectedTab,
                stories = stories,
            )
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository,
    private val storyRepository: StoryRepository,
    val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshEventsFlow()
        refreshStoryFlow()
//        refreshSuggestions()

        viewModelScope.launch {
            userRepository.getUserFlow(FirebaseAuth.getInstance().currentUser?.uid!!)
                .collect { user ->
                    viewModelState.update {
                        it.copy(user = user)
                    }
                }
        }
        refreshPostsFlow()
    }

    fun selectTab(index: Int) {
        viewModelState.update {
            it.copy(selectedTab = index)
        }
    }

    private fun refreshEventsFlow() {
//        viewModelState.update {
//            val events = eventRepository.getEvents()
//            it.copy(eventsFlow = events)
//        }
    }

    private fun refreshStoryFlow() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val stories = storyRepository.getStories()
            viewModelState.update {
                it.copy(storiesFlow = stories, isLoading = false)
            }
        }
    }

    private fun refreshPostsFlow() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val posts = postRepository.getPosts()
            viewModelState.update {
                it.copy(postsFlow = posts, isLoading = false)
            }
        }
    }

    fun onSwipeRefresh() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    postsFlow = postRepository.getPosts(),
//                    eventsFlow = eventRepository.getEvents(),
                    storiesFlow = storyRepository.getStories(),
                    user = userRepository.getCurrentUser(),
                    isLoading = false
                )
            }
        }
        refreshSuggestions()
    }

    fun refreshSuggestions() {
//        viewModelScope.launch {
//            val result = Neo4jUtil.getSuggestions()
//            viewModelState.update {
//                when (result) {
//                    is Result.Success -> it.copy(suggestions = result.data)
//                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
//                }
//            }
//        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            userRepository.blockUser(userId = userId)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.postDao.deleteWithId(postId)
            try {
                postRepository.deletePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
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
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

}
