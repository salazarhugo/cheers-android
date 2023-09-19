package com.salazar.cheers.feature.ticket.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.data.ticket.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TicketDetailsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val ticket: Ticket? = null,
)

@HiltViewModel
class TicketDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val ticketRepository: TicketRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(TicketDetailsUiState(isLoading = true))

    lateinit var ticketId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>(TICKET_ID)?.let {
            ticketId = it
        }

        viewModelScope.launch {
            ticketRepository.getTicket(id = ticketId).collect(::updateTicket)
        }
    }

    private fun updateTicket(ticket: Ticket) {
        viewModelState.update {
            it.copy(ticket = ticket)
        }
    }
}

