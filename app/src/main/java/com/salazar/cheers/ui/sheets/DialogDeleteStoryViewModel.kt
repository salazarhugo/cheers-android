package com.salazar.cheers.ui.sheets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DialogDeleteStoryViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())
    lateinit var storyID: String

    var errorMessage = MutableStateFlow<String?>(null)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("storyID")?.let {
            storyID = it
        }
    }

    fun deletePost() {
        if (!::storyID.isInitialized)
            return

        runBlocking {
            val result = storyRepository.deleteStory(storyId = storyID)
            if (result.isFailure)
                updateError(result.exceptionOrNull()?.localizedMessage)
        }
    }

    private fun updateError(message: String?) {
        errorMessage.value = message
    }
}