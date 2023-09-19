package com.salazar.cheers.data.ticket

import com.salazar.cheers.core.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    /**
     * List current user tickets.
     */
    suspend fun listTicket(userId: String): Flow<List<Ticket>>

    /**
     * Get ticket by id.
     */
    suspend fun getTicket(id: String): Flow<Ticket>
}