package com.salazar.cheers.data.ticket.impl

import android.util.Log
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.core.db.dao.TicketDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.data.ticket.TicketDataSource
import com.salazar.cheers.data.ticket.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val ticketDataSource: TicketDataSource,
): TicketRepository {
    override suspend fun listTicket(userId: String): Flow<List<Ticket>> {
        return flow {
            val localTickets = ticketDao.listTickets().first()
            if (localTickets.isNotEmpty())
                emit(localTickets.asExternalModel())

            val result = ticketDataSource.listTicket(userId = userId)
            if (result.isFailure)
                Log.e("TICKET", result.exceptionOrNull().toString())

            result.getOrNull()?.let { tickets ->
                ticketDao.insertTickets(tickets.asEntity())
            }
            emitAll(ticketDao.listTickets().map { it.asExternalModel() })
        }
    }

    override suspend fun getTicket(id: String): Flow<Ticket> {
        return ticketDao.getTicket(id = id).map { it.asExternalModel() }
    }
}