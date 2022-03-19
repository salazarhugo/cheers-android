package com.salazar.cheers.internal

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val created: Long = 0L,
    val host: String = "",
    val participants: List<String> = emptyList(),
    val showOnMap: Boolean = false,
    val imageUrl: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = Privacy.PUBLIC.name,
)

data class EventUi(
    val event: Event,
    val host: User,
    val participants: List<User>,
)