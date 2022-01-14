package com.salazar.cheers.internal

object EventType {
    const val PRIVATE = "PRIVATE"
    const val PUBLIC = "PUBLIC"
    const val FRIENDS = "FRIENDS"
    const val GROUP = "GROUP"
}

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val createdTime: String = "",
    val host: String = "",
    val participants: List<String> = emptyList(),
    val showOnMap: Boolean = false,
    val imageUrl: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = EventType.PUBLIC,
)

data class EventUi(
    val event: Event,
    val host: User,
    val participants: List<User>,
)