package com.salazar.cheers.ui.main.story.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.entities.StoryDetail
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.data.repository.UserRepository
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoryStatsViewModelUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val stories: List<StoryDetail>? = null,
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
                val storiesDetail = stories.map {
//                    StoryDetail(
//                        story = it.story,
//                        author = it.author,
//                        viewers = userRepository.getUsersWithListOfIds(it.story.seenBy)
//                    )
                }
//                viewModelState.update {
//                    it.copy(stories = storiesDetail)
//                }
            }
        }
    }

    fun onDeleteStory(storyId: String) {
        viewModelScope.launch {
            storyRepository.delete(storyId = storyId)
        }
    }
}
