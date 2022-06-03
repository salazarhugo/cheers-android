package com.salazar.cheers.ui.main.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.time.Instant
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val query: String = "",
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EventsUiState(isLoading = true))
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    val events = eventRepository.getEventFeed()
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        viewModelState.update {
            it.copy(query = query)
        }
        queryEvents(query = query)
    }

    private fun queryEvents(
        query: String = uiState.value.query.lowercase(),
        fetchFromRemote: Boolean = true,
    ) {
        events.map {
            it.filter { it.name.contains(query) }
        }
    }

    fun onGoingToggle(event: Event) {
        viewModelScope.launch {
            eventRepository.toggleGoing(event = event)
        }
    }

    fun onInterestedToggle(event: Event) {
        viewModelScope.launch {
            eventRepository.toggleInterested(event = event)
        }
    }

    fun onErrorMessageChange(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }
}

