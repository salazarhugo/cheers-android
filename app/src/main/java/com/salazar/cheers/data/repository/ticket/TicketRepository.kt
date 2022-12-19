package com.salazar.cheers.data.repository.ticket

import com.salazar.cheers.internal.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Ticket data layer.
 */
interface TicketRepository {
    /**
     * List current user tickets.
     */
    suspend fun listTicket(): Flow<List<Ticket>>
}