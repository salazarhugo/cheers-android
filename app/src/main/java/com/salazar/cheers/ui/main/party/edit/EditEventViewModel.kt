package com.salazar.cheers.ui.main.party.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.parties.data.repository.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditEventUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val party: Party? = null,
)

@HiltViewModel
class EditEventViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val partyRepository: PartyRepository,
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
            partyRepository.getParty(eventId).collect { event ->
                viewModelState.update {
                    it.copy(party = event)
                }
            }
        }
    }

    fun onSave() {
        val event = uiState.value.party ?: return

        viewModelScope.launch {
//            partyRepository.updateEvent(event.copy(name = "[UPDATED] ${event.name}"))
        }
    }
}

