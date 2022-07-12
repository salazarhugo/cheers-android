package com.salazar.cheers.ui.main.story

import androidx.lifecycle.SavedStateHandle
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
    val isPaused: Boolean = false,
    val stories: List<Story>? = null,
    val currentStep: Int = 0,
)

@HiltViewModel
class StoryViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryUiState(isLoading = true))
    private lateinit var username: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("username")?.let {
            username = it
        }

        viewModelScope.launch {
            storyRepository.getUserStory(username = username).collect { stories ->
                updateStories(stories = stories)
            }
        }
    }

    fun onPauseChange(isPaused: Boolean) {
        viewModelState.update {
            it.copy(isPaused = isPaused)
        }
    }

    private fun setNativeAd(interstitialAd: InterstitialAd?) {
        viewModelState.update {
            it.copy(interstitialAd = interstitialAd)
        }
    }

    fun onCurrentStepChange(currentStep: Int) {
        viewModelState.update {
            val max = it.stories?.size ?: 0
            it.copy(currentStep = currentStep.coerceIn((0..max)))
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

    fun onStorySeen(storyId: String) {
        viewModelScope.launch {
            storyRepository.seenStory(storyId)
        }
    }

    private fun updateStories(stories: List<Story>) {
        viewModelState.update {
            it.copy(stories = stories, isLoading = false)
        }
    }

}

