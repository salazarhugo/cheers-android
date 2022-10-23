package com.salazar.cheers.ui.main.story.feed

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StoryFeedUIAction {
    object OnBackPressed : StoryFeedUIAction()
    object OnDelete : StoryFeedUIAction()
    object OnActivity : StoryFeedUIAction()
    object OnMore : StoryFeedUIAction()
    data class OnViewed(val storyId: String) : StoryFeedUIAction()
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
            val result = storyRepository.feedStory(1, 10)
            when(result.isSuccess) {
                true -> updateUsersWithStories(usersWithStories = result.getOrNull())
                false -> updateError(result.exceptionOrNull()?.message)
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

