package com.salazar.cheers.data.ticket

import cheers.ticket.v1.ListTicketRequest
import cheers.ticket.v1.TicketServiceGrpcKt
import com.salazar.cheers.core.model.Ticket
import javax.inject.Inject

class TicketDataSource @Inject constructor(
    private val service: TicketServiceGrpcKt.TicketServiceCoroutineStub,
) {
    suspend fun listTicket(userId: String): Result<List<Ticket>> {

        val request = ListTicketRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            val response = service.listTicket(request)
            val tickets = response.ticketsList.map { it.toTicket() }
            Result.success(tickets)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}