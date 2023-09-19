package com.salazar.cheers.data.party

import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PartyDao{
    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEventT(eventId: String): Party

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEvent(eventId: String): Flow<Party>

    @Query("SELECT * FROM events WHERE hostId = :accountId")
    fun getEvents(accountId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<Party>>

    @Query("""
        SELECT * FROM events 
        """)
//        AND startDate > :now
//        ORDER BY events.startDate ASC
    fun feedParty(
//        now: Long = Date().time / 1000,
    ): Flow<List<Party>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(party: Party)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parties: List<Party>)

    @Delete
    suspend fun delete(party: Party)

    @Transaction
    @Query("DELETE FROM events WHERE events.hostId = :authorId")
    suspend fun deleteWithAuthorId(authorId: String)

    @Query("DELETE FROM events WHERE events.eventId = :eventId")
    suspend fun deleteWithId(eventId: String)

    @Update
    suspend fun update(party: Party)

    @Query("UPDATE events SET watchStatus = :watchStatus WHERE eventId = :eventId")
    suspend fun updateWatchStatus(
        eventId: String,
        watchStatus: WatchStatus,
    )

    @Query("DELETE FROM events")
    suspend fun clearAll()
}