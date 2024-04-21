package com.salazar.cheers.data.map


fun cheers.location.v1.UserLocation.toUserLocation(): UserLocation {
    return UserLocation(
        id = userId,
        picture = picture,
        name = name,
        username = username,
        latitude = latitude,
        longitude = longitude,
        verified = verified,
        lastUpdated = lastUpdated,
        locationName = locationName,
    )
}