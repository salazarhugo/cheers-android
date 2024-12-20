package com.salazar.cheers.data.map

import com.salazar.cheers.core.model.cheersUserItem
import java.util.UUID

data class UserLocation(
    val id: String,
    val picture: String,
    val verified: Boolean,
    val username: String,
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val lastUpdated: Long,
    val locationName: String,
)

val emptyUserLocation = UserLocation(
    id = "",
    username = "",
    name = "",
    verified = false,
    picture = "",
    longitude = 0.0,
    latitude = 0.0,
    locationName = "",
    lastUpdated = 0L,
)


val cheersUserLocation = cheersUserItem.toUserLocation()

val cheersUserLocationList = List(20) {
    cheersUserLocation.copy(id = UUID.randomUUID().toString())
}
