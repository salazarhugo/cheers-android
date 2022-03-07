package com.salazar.cheers.ui.main.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
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
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshStoryFlow()
    }

    fun onStoryOpen(storyId: String) {
        viewModelScope.launch {
            storyRepository.seenStory(storyId)
        }
     }

    private fun refreshStoryFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(storiesFlow = storyRepository.getStories(), isLoading = false)
        }
    }
}

