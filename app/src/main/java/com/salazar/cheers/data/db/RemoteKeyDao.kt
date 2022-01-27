package com.salazar.cheers.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.salazar.cheers.data.entities.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE postId = :postId")
    suspend fun remoteKeyByPostId(postId: String): RemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}