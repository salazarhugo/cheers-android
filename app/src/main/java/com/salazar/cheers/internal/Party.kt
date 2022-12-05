package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Party(
    @PrimaryKey
    @ColumnInfo(name = "eventId")
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val createTime: Long = 0,
    val hostId: String = "",
    val hostName: String = "",
    val price: Int = 0,
    val participants: List<String> = emptyList(),
    val showGuestList: Boolean = false,
    val showOnMap: Boolean = false,
    val interested: Boolean = false,
    val interestedCount: Int = 0,
    val going: Boolean = false,
    val goingCount: Int = 0,
    val bannerUrl: String = "",
    val address: String = "",
    val mutualProfilePictureUrls: List<String> = emptyList(),
    val mutualUsernames: List<String> = emptyList(),
    val mutualCount: Int = 0,
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val privacy: Privacy = Privacy.PUBLIC,
    val type: String = Privacy.PUBLIC.name,
    val accountId: String = "",
)

data class EventUi(
    val party: Party,
    val host: User,
    val participants: List<User>,
)

data class CreatePartyRequest(
    val party: A
)

data class A(
    val name: String = "mobile-cheers",
    val latlng: LatLng = LatLng(),
    val startDate: Timestamp = Timestamp(),
    val endDate: Timestamp = Timestamp(),
)

data class Timestamp(
    val seconds: Int = 0,
    val nanos: Int = 0,
)

data class LatLng(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
