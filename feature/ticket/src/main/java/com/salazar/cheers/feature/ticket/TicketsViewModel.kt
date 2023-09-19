package com.salazar.cheers.feature.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.domain.list_ticket.ListTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TicketsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val tickets: List<Ticket>? = null,
)

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val listTicketUseCase: ListTicketUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(TicketsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        onSwipeRefresh()
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            listTicketUseCase().collect(::updateTickets)
        }
    }

    private fun updateTickets(tickets: List<Ticket>) {
        viewModelState.update {
            it.copy(tickets = tickets, isLoading = false)
        }
    }
}

sealed class TicketsUIAction {
    object OnSwipeRefresh : TicketsUIAction()
    object OnBackPressed : TicketsUIAction()
    data class OnTicketClick(val ticketId: String) : TicketsUIAction()
}
