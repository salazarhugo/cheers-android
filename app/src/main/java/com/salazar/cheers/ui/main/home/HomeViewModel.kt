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
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.internal.EventUi
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.SuggestionUser
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
        val postsFlow: Flow<PagingData<PostFeed>>,
        val eventsFlow: Flow<PagingData<EventUi>>?,
        val storiesFlow: Flow<PagingData<Story>>?,
        val listState: LazyListState = LazyListState(),
        val likes: Set<String>,
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
    val postsFlow: Flow<PagingData<PostFeed>>? = null,
    val eventsFlow: Flow<PagingData<EventUi>>? = null,
    val storiesFlow: Flow<PagingData<Story>>? = null,
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
            )
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PostRepository,
    private val eventRepository: EventRepository,
    private val storyRepository: StoryRepository,
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
        refreshPostsFlow()
        refreshEventsFlow()
        refreshStoryFlow()
        refreshSuggestions()

        // Observe for like changes in the repo layer
        viewModelScope.launch {
            repository.observeLikes().collect { likes ->
                viewModelState.update { it.copy(likes = likes) }
            }
        }
    }

    fun selectTab(index: Int) {
        viewModelState.update {
            it.copy(selectedTab = index)
        }
    }

    private fun refreshEventsFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(eventsFlow = eventRepository.getEvents(), isLoading = false)
        }
    }

    private fun refreshStoryFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(storiesFlow = storyRepository.getStories(), isLoading = false)
        }
    }

    private fun refreshPostsFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(postsFlow = repository.getPosts(), isLoading = false)
        }
    }

    fun refresh() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(
                postsFlow = repository.getPosts(),
                eventsFlow = eventRepository.getEvents(),
                storiesFlow = storyRepository.getStories(),
                isLoading = false
            )
        }
        refreshSuggestions()
    }

    fun refreshSuggestions() {
        viewModelScope.launch {
            val result = Neo4jUtil.getSuggestions()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(suggestions = result.data)
                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
                }
            }
        }
    }

    fun toggleLike(post: Post) {
        val likes = if (post.liked) post.likes - 1 else post.likes + 1
        viewModelScope.launch {
            repository.postDao.update(post.copy(liked = !post.liked, likes = likes))
        }
        if (post.liked)
            unlikePost(post.id)
        else
            likePost(post.id)
    }

    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.unlikePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun unfollowUser(username: String) {
        viewModelScope.launch {
            Neo4jUtil.unfollowUser(username = username)
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.likePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.postDao.deleteWithId(postId)
            try {
                Neo4jUtil.deletePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }

    private fun setNativeAd(nativeAd: NativeAd) {
        viewModelState.update {
            it.copy(nativeAd = nativeAd)
        }
    }

    fun initNativeAdd(context: Context) {
//        return
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
