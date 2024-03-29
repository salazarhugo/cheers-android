package com.salazar.cheers.feature.home.navigation.home

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.story.v1.UserWithStories
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.paging.DefaultPaginator
import com.salazar.cheers.core.util.playback.AndroidAudioPlayer
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val homeUseCases: HomeUseCases,
    private val audioPlayer: AndroidAudioPlayer,
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
            updateIsLoading(true)
            postRepository.fetchPostFeed(nextPage, 5)
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
        refreshFriendNotes()

        collectAudioState()

        viewModelScope.launch {
            homeUseCases.getAccountUseCase()
                .collect(::updateAccount)
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

//        viewModelScope.launch {
//            homeUseCases.listStoryFeed()
//                .collect(::updateStories)
//        }

        viewModelScope.launch {
            homeUseCases.listNoteFeed()
                .collect(::updateNotes)
        }
    }

    private fun collectAudioState() {
        viewModelScope.launch {
            audioPlayer.getAudioState().collect(::updateAudioState)
        }
    }

    private fun updateAudioState(audioState: AudioState) {
        viewModelState.update {
            it.copy(audioState = audioState)
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            homeUseCases.listSuggestions().onSuccess {
                updateSuggestions(it)
            }
        }
    }

    private fun updateSuggestions(suggestions: List<UserItem>) {
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

    private fun updateNotes(notes: List<Note>) {
        viewModelState.update {
            it.copy(notes = notes)
        }
    }

    fun loadNextPosts() {
        println("LOAD NEXT POSTS")
        updateIsLoading(true)
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    fun onSwipeRefresh() {
        updateIsLoading(true)
        viewModelScope.launch {
            paginator.reset()
            paginator.loadNextItems()
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

    private fun updateAccount(account: Account?) {
        viewModelState.update {
            it.copy(account = account)
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

    fun onAudioClick(postID: String, audioUrl: String) {
        viewModelState.update { it.copy(audioPostID = postID) }
        viewModelScope.launch {
            audioPlayer.playFromUrl(audioUrl)
        }
    }
}

