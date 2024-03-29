package com.salazar.cheers.data.map

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
