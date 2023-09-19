package com.salazar.cheers.domain.list_ticket

import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.data.ticket.TicketRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListTicketUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val repository: TicketRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        userId: String? = null
    ): Flow<List<Ticket>> {
        return withContext(dispatcher) {
            val uid = userId ?: getAccountUseCase().first()?.id ?: return@withContext flowOf(emptyList())

            return@withContext repository.listTicket(userId = uid)
        }
    }
}