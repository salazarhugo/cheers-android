package com.salazar.cheers.data.map

import cheers.location.v1.Location
import com.salazar.cheers.data.map.UserLocation


fun Location.toUserLocation(): com.salazar.cheers.data.map.UserLocation {
    return com.salazar.cheers.data.map.UserLocation(
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