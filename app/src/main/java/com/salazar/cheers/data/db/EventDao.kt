package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Transaction
    @Query("SELECT * FROM events WHERE accountId = :accountId ORDER BY events.created DESC")
    fun pagingSourceFeed(accountId: String = FirebaseAuth.getInstance().currentUser?.uid!!): PagingSource<Int, Event>

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEventT(eventId: String): Event

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    fun getEvent(eventId: String): Flow<Event>

    @Query("SELECT * FROM events WHERE hostId = :accountId")
    fun getEvents(accountId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Delete
    suspend fun delete(event: Event)

    @Transaction
    @Query("DELETE FROM events WHERE events.hostId = :authorId")
    suspend fun deleteWithAuthorId(authorId: String)

    @Query("DELETE FROM events WHERE events.eventId = :eventId")
    suspend fun deleteWithId(eventId: String)

    @Update
    suspend fun update(event: Event)

    @Query("UPDATE events SET interested = :interested, interestedCount = :count WHERE eventId = :eventId")
    suspend fun updateInterested(
        eventId: String,
        interested: Boolean,
        count: Int
    )

    @Query("UPDATE events SET going = :going, goingCount = :count WHERE eventId = :eventId")
    suspend fun updateGoing(
        eventId: String,
        going: Boolean,
        count: Int
    )

    @Transaction
    suspend fun toggleGoing(eventId: String) {
        val event = getEventT(eventId)
        val going = !event.going
        val count = if (event.going) event.goingCount - 1 else event.goingCount + 1
        updateGoing(eventId, going, count)
    }

    @Transaction
    suspend fun toggleInterested(eventId: String) {
        val event = getEventT(eventId)
        val interested = !event.interested
        val count = if (event.interested) event.interestedCount - 1 else event.interestedCount + 1
        updateInterested(eventId, interested, count)
    }

    @Query("DELETE FROM events")
    suspend fun clearAll()
}