package com.salazar.cheers.ui.main.story.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoryStatsViewModelUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val stories: List<Story>? = null,
)

@HiltViewModel
class StoryStatsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryStatsViewModelUiState(isLoading = true))
    private lateinit var storyId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("storyId")?.let {
            storyId = it
        }
        viewModelScope.launch {
            storyRepository.getMyStories().collect { stories ->
                val stories = stories
                viewModelState.update {
                    it.copy(stories = stories)
                }
            }
        }
    }

    fun onDeleteStory(storyId: String) {
        viewModelScope.launch {
            storyRepository.delete(storyId = storyId)
        }
    }
}
