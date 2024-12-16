package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable
import java.util.Date
import java.util.concurrent.TimeUnit

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
    val city: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val privacy: Privacy = Privacy.PUBLIC,
    val type: String = Privacy.PUBLIC.name,
    val accountId: String = "",
    val lineup: List<String> = emptyList(),
    val musicGenres: List<String> = emptyList(),
    val mutualGoing: Map<String, String> = emptyMap(),
)

enum class WatchStatus {
    INTERESTED,
    GOING,
    UNWATCHED,
}

val mirageParty = Party(
    id = "MIRAGE_PARTY_ID",
    name = "Mirage presents Serum w/ Sara Bluma, Aaron Julian, Gianni",
    mutualGoing = mapOf("esf" to "cheers", "afw" to "mcdo", "wf" to "nike"),
    hostName = "Cabaret Sauvage",
    price = 32455,
)

val duplexParty = Party(
    id = "DUPLEX_PARTY_ID",
    name = "Duplex presents Serum w/ Sara Bluma, Aaron Julian, Gianni",
    mutualGoing = mapOf("esf" to "cheers", "afw" to "mcdo", "wf" to "nike"),
    hostName = "Duplex",
    price = 88499,
    goingCount = 2523,
    interestedCount = 13200,
    address = "2 Avenue Foch, 75016 Paris",
    startDate = Date().time / 1000,
    endDate = (Date().time + TimeUnit.HOURS.toMillis(7)) / 1000,
    musicGenres = listOf("POP", "TECHNO"),
    lineup = listOf("Cheers", "DJ Snake"),
)
