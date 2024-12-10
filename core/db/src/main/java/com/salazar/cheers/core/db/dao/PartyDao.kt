package com.salazar.cheers.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.salazar.cheers.core.db.model.PartyEntity
import com.salazar.cheers.core.model.WatchStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PartyDao{
    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEventT(eventId: String): PartyEntity

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEvent(eventId: String): Flow<PartyEntity?>

    @Query("SELECT * FROM events WHERE hostId = :accountId")
    fun getEvents(accountId: String = ""): Flow<List<PartyEntity>>

    @Query("""
        SELECT * FROM events 
        WHERE endDate > :now
        ORDER BY events.startDate ASC
        """)
    fun feedParty(
        now: Long = Date().time / 1000,
    ): Flow<List<PartyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(party: PartyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parties: List<PartyEntity>)

    @Delete
    suspend fun delete(party: PartyEntity)

    @Transaction
    @Query("DELETE FROM events WHERE events.hostId = :authorId")
    suspend fun deleteWithAuthorId(authorId: String)

    @Query("DELETE FROM events WHERE events.eventId = :eventId")
    suspend fun deleteWithId(eventId: String)

    @Update
    suspend fun update(party: PartyEntity)

    @Query("UPDATE events SET watchStatus = :watchStatus WHERE eventId = :eventId")
    suspend fun updateWatchStatus(
        eventId: String,
        watchStatus: WatchStatus,
    )

    @Query("DELETE FROM events")
    suspend fun clearAll()
}