package com.salazar.cheers.ui.main.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventMoreSheetUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

@HiltViewModel
class EventMoreSheetViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EventMoreSheetUiState(isLoading = true))
    private lateinit var eventId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("eventId")?.let {
            this.eventId = it
        }
    }

    fun onHide() {
        viewModelScope.launch {
            eventRepository.hideEvent(eventId)
        }
    }
}

