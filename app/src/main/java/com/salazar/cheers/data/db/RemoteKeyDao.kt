package com.salazar.cheers.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.salazar.cheers.data.db.entities.EventRemoteKey
import com.salazar.cheers.data.db.entities.RemoteKey
import com.salazar.cheers.data.db.entities.StoryRemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE postId = :postId")
    suspend fun remoteKeyByPostId(postId: String): RemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}

@Dao
interface StoryRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<StoryRemoteKey>)

    @Query("SELECT * FROM story_remote_keys WHERE storyId = :storyId")
    suspend fun remoteKeyByStoryId(storyId: String): StoryRemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}

@Dao
interface EventRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<EventRemoteKey>)

    @Query("SELECT * FROM event_remote_keys WHERE eventId = :eventId")
    suspend fun remoteKeyByEventId(eventId: String): EventRemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}
