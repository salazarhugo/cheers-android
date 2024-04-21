package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Party(
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
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val privacy: Privacy = Privacy.PUBLIC,
    val type: String = Privacy.PUBLIC.name,
    val accountId: String = "",
    val mutualGoing: Map<String, String> = emptyMap(),
)

enum class WatchStatus {
    INTERESTED,
    GOING,
    UNWATCHED,
}