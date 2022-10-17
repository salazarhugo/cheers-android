package com.salazar.cheers.ui.main.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.paging.DefaultPaginator
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.SuggestionUser
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val posts: List<Post> = emptyList(),
    val storiesFlow: Flow<PagingData<Story>>? = null,
    val stories: PagingData<Story>? = null,
    val listState: LazyListState = LazyListState(),
    val likes: Set<String> = emptySet(),
    val user: User? = null,
    val postSheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    val suggestions: List<SuggestionUser>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val nativeAd: NativeAd? = null,
    val selectedTab: Int = 0,
    val notificationCount: Int = 0,
    val endReached: Boolean = false,
    val page: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())
    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    private val paginator = DefaultPaginator(
        initialKey = 0,
        onLoadUpdated = {
            updateIsLoading(isLoading = it)
        },
        onRequest = { nextPage ->
            postRepository.getPostFeed(nextPage, 1)
        },
        getNextKey = {
            uiState.value.page + 1
        },
        onError = {
            updateError(it?.localizedMessage)
        },
        onSuccess = { items, newKey ->
            Log.d("ITEMS", items.toString())
            viewModelState.update {
                it.copy(
                    page = newKey,
                    endReached = items.isEmpty()
                )
            }
        }
    )

    lateinit var postID: String

    init {
        stateHandle.get<String>("postID")?.let {
            postID = it
        }
        loadNextItems()
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect { user ->
                viewModelState.update {
                    it.copy(user = user)
                }
            }
        }
        viewModelScope.launch {
            postRepository.getPostFeedFlow().collect { posts ->
                viewModelState.update {
                    it.copy(posts = posts)
                }
            }
        }
        refreshStoryFlow()
    }

    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
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

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    fun onSwipeRefresh() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            userRepository.getUserSignIn(userId = FirebaseAuth.getInstance().currentUser?.uid!!)
                .collect {
                    if (it is Resource.Success)
                        updateUser(user = it.data)
                }
        }

        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    storiesFlow = storyRepository.getStories(),
                    isLoading = false
                )
            }
        }
        viewModelState.update { it.copy(endReached = false) }
        paginator.reset()
    }

    private fun updateError(errorMessages: String?) {
    }

    private fun incrementPage() {
        viewModelState.update {
            it.copy(page = it.page + 1)
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

sealed class HomeUIAction {
    object OnActivityClick : HomeUIAction()
    object OnSearchClick : HomeUIAction()
    object OnSwipeRefresh : HomeUIAction()
    object OnAddStoryClick : HomeUIAction()
    object OnAddPostClick : HomeUIAction()
    object OnLoadNextItems : HomeUIAction()
    data class OnCommentClick(val postID: String) : HomeUIAction()
    data class OnLikeClick(val post: Post) : HomeUIAction()
    data class OnStoryClick(val userID: String) : HomeUIAction()
    data class OnUserClick(val userID: String) : HomeUIAction()
    data class OnPostClick(val postID: String) : HomeUIAction()
    data class OnPostMoreClick(val postID: String) : HomeUIAction()
}
