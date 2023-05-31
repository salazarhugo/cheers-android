package com.salazar.cheers.ui.main.party.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.WatchStatus
import com.salazar.cheers.parties.data.repository.PartyRepository
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
    private val partyRepository: PartyRepository,
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
            partyRepository.getParty(partyId = eventId).collect { event ->
                onEventChange(party = event)
            }
        }
    }

    private fun onEventChange(party: Party) {
        viewModelState.update {
            it.copy(party = party)
        }
    }

    fun onWatchStatusChange(watchStatus: WatchStatus) {
        viewModelScope.launch {
            partyRepository.setWatchStatus(
                partyId = eventId,
                watchStatus = watchStatus,
            )
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
//            partyRepository.deleteEvent(eventId = eventId)
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }
}
