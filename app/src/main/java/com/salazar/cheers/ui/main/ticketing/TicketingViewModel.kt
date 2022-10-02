package com.salazar.cheers.ui.main.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.PartyRepository
import com.salazar.cheers.internal.Party
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TicketingTicket(
    val title: String,
    val description: String,
    val price: Int,
)

data class TicketingUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val party: Party? = null,
    val tickets: List<TicketingTicket>? = null,
)

@HiltViewModel
class TicketingViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(TicketingUiState(isLoading = true))
    lateinit var eventId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("eventId")?.let {
            eventId = it
        }
        refreshEvent()
        updateTickets()
    }

    fun updateTickets() {
        val tickets = listOf<TicketingTicket>(
            TicketingTicket(
                title = "Entree Standard",
                description = "Avec une boisson offerte",
                price = 4500,
            ),
            TicketingTicket(
                title = "Entree Premium",
                description = "Avec une bouteille premium",
                price = 20000,
            ),
            TicketingTicket(
                title = "Entree VIP",
                description = "Avec un magnum premium",
                price = 45000,
            ),
        )
        viewModelState.update {
            it.copy(tickets =  tickets)
        }
    }

    private fun refreshEvent() {
        viewModelScope.launch {
            partyRepository.getEvent(eventId = eventId).collect { event ->
                updateEvent(party = event)
            }
        }
    }

    private fun updateEvent(party: Party) {
        viewModelState.update {
            it.copy(party = party)
        }
    }


    fun onSwipeRefresh() {
        refreshTicketing()
    }

    private fun refreshTicketing() {
    }

    private fun filterTicketing() {
    }


    private fun updateError(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

}

sealed class TicketingUIAction {
    object OnPersonalClick : TicketingUIAction()
}

