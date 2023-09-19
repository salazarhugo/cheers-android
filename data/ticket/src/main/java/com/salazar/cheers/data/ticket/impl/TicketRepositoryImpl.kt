package com.salazar.cheers.data.ticket.impl

import android.util.Log
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.data.ticket.TicketDao
import com.salazar.cheers.data.ticket.TicketDataSource
import com.salazar.cheers.data.ticket.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val ticketDataSource: TicketDataSource,
): TicketRepository {
    override suspend fun listTicket(userId: String): Flow<List<Ticket>> {
        return flow {
            val localTickets = ticketDao.listTickets().first()
            if (localTickets.isNotEmpty())
                emit(localTickets)

            val result = ticketDataSource.listTicket(userId = userId)
            if (result.isFailure)
                Log.e("TICKET", result.exceptionOrNull().toString())

            result.getOrNull()?.let { tickets ->
                ticketDao.insertTickets(tickets)
            }
            emitAll(ticketDao.listTickets())
        }
    }

    override suspend fun getTicket(id: String): Flow<Ticket> {
        return ticketDao.getTicket(id = id)
    }
}