package com.salazar.cheers.ui.main.party

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.party.PartyRepository
import com.salazar.cheers.internal.Party
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val query: String = "",
    val parties: List<Party>? = null,
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val partyRepository: PartyRepository,
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
        viewModelScope.launch {
            partyRepository.feedParty(1, 10).collect {
                updateParties(it)
            }
        }
        viewModelScope.launch {
            val result = partyRepository.fetchFeedParty(1, 10)
            when(result.isSuccess) {
                true -> updateParties(result.getOrNull())
                false -> updateError("Couldn't refresh party feed")
            }
        }
    }

    private fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun updateParties(parties: List<Party>?) {
        viewModelState.update {
            it.copy(parties = parties)
        }
    }

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
    }

    fun onGoingToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onInterestedToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onErrorMessageChange(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }
}

