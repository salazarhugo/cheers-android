package com.salazar.cheers.ui.main.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StoryUIAction {
    object OnTap : StoryUIAction()
    object OnDelete : StoryUIAction()
    object OnSeen : StoryUIAction()
    object OnActivity : StoryUIAction()
}


data class StoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val input: String = "",
    val interstitialAd: InterstitialAd? = null,
    val pause: Boolean = false,
    val storiesFlow: Flow<PagingData<Story>>? = null,
)

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value
        )

    init {
        refreshStoryFlow()
    }

    fun onPauseChange(pause: Boolean) {
        viewModelState.update {
            it.copy(pause = pause)
        }
    }

    private fun setNativeAd(interstitialAd: InterstitialAd?) {
        viewModelState.update {
            it.copy(interstitialAd = interstitialAd)
        }
    }

    fun onDelete(storyId: String) {
        viewModelScope.launch {
            storyRepository.delete(storyId = storyId)
        }
    }

    fun onSendReaction(
        story: Story,
        text: String
    ) {
//        storyRepository.sendReaction(story.authorId, text)
        onInputChange("")
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }

    fun onStoryOpen(storyId: String) {
        viewModelScope.launch {
            storyRepository.seenStory(storyId)
        }
    }

    private fun refreshStoryFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch() {
            viewModelState.update {
                it.copy(storiesFlow = storyRepository.getStories(), isLoading = false)
            }
        }
    }

}

