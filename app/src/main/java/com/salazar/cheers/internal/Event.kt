package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.ads.AdRequest
import com.google.common.hash.HashCode.fromInt
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    @ColumnInfo(name = "eventId")
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val created: Long = 0L,
    val hostId: String = "",
    val hostName: String = "",
    val price: Int = 0,
    val participants: List<String> = emptyList(),
    val showOnMap: Boolean = false,
    val interested: Boolean = false,
    val interestedCount: Int = 0,
    val goingCount: Int = 0,
    val imageUrl: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val privacy: Privacy = Privacy.PUBLIC,
    val type: String = Privacy.PUBLIC.name,
    val accountId: String = "",
)

data class EventUi(
    val event: Event,
    val host: User,
    val participants: List<User>,
)

