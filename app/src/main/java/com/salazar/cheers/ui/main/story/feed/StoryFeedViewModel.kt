package com.salazar.cheers.ui.main.story.feed

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.domain.models.UserWithStories
import com.salazar.cheers.domain.usecase.feed_story.ListStoryFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StoryFeedUIAction {
    object OnBackPressed : StoryFeedUIAction()
    object OnDelete : StoryFeedUIAction()
    object OnActivity : StoryFeedUIAction()
    data class OnMoreClick(val storyId: String) : StoryFeedUIAction()
    data class OnViewed(val storyId: String) : StoryFeedUIAction()
    data class OnToggleLike(val storyId: String, val liked: Boolean) : StoryFeedUIAction()
    data class OnUserClick(val userId: String) : StoryFeedUIAction()
}


data class StoryFeedUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val input: String = "",
    val interstitialAd: InterstitialAd? = null,
    val isPaused: Boolean = false,
    val usersWithStories: List<UserWithStories>? = null,
    val page: Int = 0,
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
)

@HiltViewModel
class StoryFeedViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storyRepository: StoryRepository,
    private val listStoryFeedUseCase: ListStoryFeedUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryFeedUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value
        )

    init {
        stateHandle.get<Int>("page")?.let {
            updatePage(it)
        }

        viewModelScope.launch {
            listStoryFeedUseCase().collect {
                updateUsersWithStories(usersWithStories = it)
            }
        }
    }

    private fun updateError(message: String?) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun onViewed(storyId: String) {
        viewModelScope.launch {
            val result = storyRepository.viewStory(storyId = storyId)
            when(result.isSuccess) {
                true -> Unit
                false -> updateError(result.exceptionOrNull()?.localizedMessage)
            }
        }
    }

    fun onToggleLike(storyId: String, liked: Boolean) {
        viewModelScope.launch {
            val result =
                if (liked)
                    storyRepository.likeStory(storyId = storyId)
                else
                    storyRepository.unlikeStory(storyId = storyId)
            when(result.isSuccess) {
                true -> Unit
                false -> updateError(result.exceptionOrNull()?.localizedMessage)
            }
        }
    }

    fun updatePage(page: Int) {
        viewModelState.update {
            it.copy(page = page)
        }
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }

    private fun updateUsersWithStories(usersWithStories: List<UserWithStories>?) {
        viewModelState.update {
            it.copy(usersWithStories = usersWithStories, isLoading = false)
        }
    }
}

