package com.salazar.cheers.core.data.remote

import cheers.ticket.v1.ListTicketRequest
import cheers.ticket.v1.TicketServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.mapper.toTicket
import com.salazar.cheers.core.data.internal.Ticket
import javax.inject.Inject

class TicketDataSource @Inject constructor(
    private val service: TicketServiceGrpcKt.TicketServiceCoroutineStub,
) {
    suspend fun listTicket(): Result<List<Ticket>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = ListTicketRequest.newBuilder()
            .setUserId(uid)
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