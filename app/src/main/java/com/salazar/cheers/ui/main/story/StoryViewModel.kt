package com.salazar.cheers.ui.main.story

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.salazar.cheers.core.db.model.Story
import com.salazar.cheers.data.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StoryUIAction {
    object OnTap : StoryUIAction()
    object OnDelete : StoryUIAction()
    object OnSeen : StoryUIAction()
    object OnActivity : StoryUIAction()
    object OnMore : StoryUIAction()
}


data class StoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val input: String = "",
    val interstitialAd: InterstitialAd? = null,
    val isPaused: Boolean = false,
    val stories: List<Story>? = null,
    val currentStep: Int = 0,
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
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

    fun openSheet() {
        viewModelScope.launch {
            uiState.value.sheetState.show()
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
            storyRepository.deleteStory(storyId = storyId)
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
            storyRepository.viewStory(storyId)
        }
    }

    private fun updateStories(stories: List<Story>) {
        viewModelState.update {
            it.copy(stories = stories, isLoading = false)
        }
    }

}

