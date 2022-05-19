package com.salazar.cheers.ui.main.event.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class EditEventUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val event: Event? = null,
)

@HiltViewModel
class EditEventViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EditEventUiState(isLoading = true))
    private lateinit var eventId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("eventId")?.let { eventId ->
            this.eventId = eventId
        }

        viewModelScope.launch {
            eventRepository.getEvent(eventId).collect { event ->
                viewModelState.update {
                    it.copy(event = event)
                }
            }
        }
    }

    fun onSave() {
        val event = uiState.value.event ?: return

        viewModelScope.launch {
            eventRepository.updateEvent(event.copy(name = "[UPDATED] ${event.name}"))
        }
    }
}

