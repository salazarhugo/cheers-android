package com.salazar.cheers.ui.main.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.salazar.cheers.data.repository.PartyRepository
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

