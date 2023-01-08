package com.salazar.cheers.ui.main.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.ticket.TicketRepository
import com.salazar.cheers.internal.Ticket
import com.salazar.cheers.ui.main.home.HomeUIAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class TicketsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val tickets: List<Ticket> = emptyList(),
)

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(TicketsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            ticketRepository.listTicket().collect(::updateTickets)
        }
    }

    private fun updateTickets(tickets: List<Ticket>) {
        viewModelState.update {
            it.copy(tickets = tickets)
        }
    }
}

sealed class TicketsUIAction {
    object OnSwipeRefresh : TicketsUIAction()
    object OnBackPressed : TicketsUIAction()
    data class OnTicketClick(val ticketId: String) : TicketsUIAction()
}
