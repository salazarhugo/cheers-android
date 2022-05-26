package com.salazar.cheers.ui.main.story.stats

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.entities.StoryDetail
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoryStatsViewModelUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val stories: List<StoryDetail>? = null,
)

class StoryStatsViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    @Assisted private val storyId: String,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(StoryStatsViewModelUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
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

    @AssistedFactory
    interface StoryStatsViewModelFactory {
        fun create(storyId: String): StoryStatsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: StoryStatsViewModelFactory,
            storyId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(storyId = storyId) as T
            }
        }
    }
}

@Composable
fun storyStatsViewModel(storyId: String): StoryStatsViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).storyStatsViewModelFactory()

    return viewModel(factory = StoryStatsViewModel.provideFactory(factory, storyId = storyId))
}
