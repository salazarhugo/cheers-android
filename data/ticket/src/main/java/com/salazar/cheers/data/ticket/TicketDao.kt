package com.salazar.cheers.data.ticket

import androidx.room.*
import com.salazar.cheers.core.model.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tickets: List<Ticket>)

    @Query("DELETE FROM tickets")
    suspend fun clear()

    @Query("SELECT * FROM tickets")
    fun listTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    fun getTicket(id: String): Flow<Ticket>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<Ticket>) {
        clear()
        insert(tickets)
    }
}