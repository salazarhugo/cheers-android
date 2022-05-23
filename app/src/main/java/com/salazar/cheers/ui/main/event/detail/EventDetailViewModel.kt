package com.salazar.cheers.ui.main.event.detail

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.EventUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
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
        val event: Event,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : EventDetailUiState
}

private data class EventDetailViewModelState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): EventDetailUiState =
        if (event == null) {
            EventDetailUiState.NoEvents(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            EventDetailUiState.HasEvent(
                event = event,
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
                onEventChange(event = event)
            }
        }
    }

    private fun onEventChange(event: Event) {
        viewModelState.update {
            it.copy(event = event)
        }
    }

    fun onGoingToggle() {
//        viewModelScope.launch {
//            eventRepository.toggleGoing(eventId = eventId)
//        }
    }

    fun onInterestedToggle() {
        viewModelScope.launch {
            eventRepository.toggleInterested(eventId = eventId)
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
