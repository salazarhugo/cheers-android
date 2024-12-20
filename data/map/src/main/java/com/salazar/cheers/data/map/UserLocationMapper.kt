package com.salazar.cheers.data.map

import com.salazar.cheers.core.model.UserItem


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

fun UserLocation.toUserItem(): UserItem {
    return UserItem(
        id = id,
        picture = picture,
        name = name,
        username = username,
        verified = verified,
    )
}

fun UserItem.toUserLocation(): UserLocation {
    return UserLocation(
        id = id,
        picture = picture.orEmpty(),
        name = name,
        username = username,
        verified = verified,
        longitude = 0.0,
        latitude = 0.0,
        locationName = "",
        lastUpdated = 0L,
    )
}
