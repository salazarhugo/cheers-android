package com.salazar.cheers.ui.main.event.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.Party
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EventDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoEvents(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : EventDetailUiState

    data class HasEvent(
        val party: Party,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : EventDetailUiState
}

private data class EventDetailViewModelState(
    val party: Party? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): EventDetailUiState =
        if (party == null) {
            EventDetailUiState.NoEvents(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            EventDetailUiState.HasEvent(
                party = party,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EventDetailViewModelState(isLoading = true))
    private lateinit var eventId: String

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        stateHandle.get<String>("eventId")?.let { eventId ->
            this.eventId = eventId
        }

        viewModelScope.launch {
            eventRepository.getEvent(eventId = eventId).collect { event ->
                onEventChange(party = event)
            }
        }
    }

    private fun onEventChange(party: Party) {
        viewModelState.update {
            it.copy(party = party)
        }
    }

    fun onGoingToggle() {
//        viewModelScope.launch {
//            eventRepository.toggleGoing(eventId = eventId)
//        }
    }

    fun onGoingToggle(party: Party) {
        viewModelScope.launch {
            eventRepository.toggleGoing(party = party)
        }
    }

    fun onInterestedToggle(party: Party) {
        viewModelScope.launch {
            eventRepository.toggleInterested(party = party)
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId = eventId)
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }
}
