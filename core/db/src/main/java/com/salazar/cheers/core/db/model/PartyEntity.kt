package com.salazar.cheers.core.db.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.WatchStatus

@Entity(tableName = "events")
@Immutable
data class PartyEntity(
    @PrimaryKey
    @ColumnInfo(name = "eventId")
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val createTime: Long = 0,
    val hostId: String = "",
    val isHost: Boolean = false,
    val hostName: String = "",
    val price: Int? = null,
    val participants: List<String> = emptyList(),
    val showGuestList: Boolean = false,
    val showOnMap: Boolean = false,
    val interestedCount: Int = 0,
    val goingCount: Int = 0,
    val watchStatus: WatchStatus = WatchStatus.UNWATCHED,
    val bannerUrl: String = "",
    val address: String = "",
    val city: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val privacy: Privacy = Privacy.PUBLIC,
    val type: String = Privacy.PUBLIC.name,
    val accountId: String = "",
    val mutualGoing: Map<String, String> = emptyMap(),
)

fun PartyEntity.asExternalModel() = Party(
    id = id,
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    createTime = createTime,
    hostId = hostId,
    isHost = isHost,
    hostName = hostName,
    price = price,
    participants = participants,
    showGuestList = showGuestList,
    showOnMap = showOnMap,
    interestedCount = interestedCount,
    goingCount = goingCount,
    watchStatus = watchStatus,
    bannerUrl = bannerUrl,
    address = address,
    locationName = locationName,
    city = city,
    latitude = latitude,
    longitude = longitude,
    privacy = privacy,
    type = type,
    accountId = accountId,
    mutualGoing = mutualGoing,
)

fun Party.asEntity() = PartyEntity(
    id = id,
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    createTime = createTime,
    hostId = hostId,
    isHost = isHost,
    hostName = hostName,
    price = price,
    participants = participants,
    showGuestList = showGuestList,
    showOnMap = showOnMap,
    interestedCount = interestedCount,
    goingCount = goingCount,
    watchStatus = watchStatus,
    bannerUrl = bannerUrl,
    address = address,
    locationName = locationName,
    city = city,
    latitude = latitude,
    longitude = longitude,
    privacy = privacy,
    type = type,
    accountId = accountId,
    mutualGoing = mutualGoing,
)

fun List<PartyEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Party>.asEntity() = this.map { it.asEntity() }
