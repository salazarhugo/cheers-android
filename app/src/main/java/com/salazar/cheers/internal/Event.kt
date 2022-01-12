package com.salazar.cheers.internal

import androidx.room.Entity

//object PostType {
//    const val TEXT = "TEXT"
//    const val IMAGE = "IMAGE"
//    const val VIDEO = "VIDEO"
//}

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val duration: Long,
    val createdTime: String = "",
    val hosts: List<User> = emptyList(),
    val showOnMap: Boolean = false,
    val photoUrl: String = "",
    val location: Location,
)

data class Location(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
